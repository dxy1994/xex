package com.exam.ai.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * DeepSeek HTTP API 客户端
 * <p>
 * 使用 Spring RestClient 调用 DeepSeek 兼容 OpenAI 格式的接口。
 * 支持多模型：通过 modelKey 从 {@link DeepSeekProperties} 查找对应的模型参数。
 * 支持流式输出（SSE）。
 */
@Slf4j
@Component
public class DeepSeekClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final DeepSeekProperties properties;
    private final HttpClient httpClient;

    public DeepSeekClient(DeepSeekProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.deepseek.com")
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * 发送聊天请求（指定模型 key）
     *
     * @param modelKey     模型标识（如 "chat"、"reasoner"），对应 yml 中 models 下的 key
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 回复内容
     */
    public String chat(String modelKey, String systemPrompt, String userPrompt) {
        DeepSeekProperties.ModelConfig config = properties.getModels().get(modelKey);
        if (config == null) {
            throw new IllegalArgumentException("未找到模型配置: " + modelKey);
        }

        log.debug("【DeepSeek】发送请求, modelKey={}, model={}, systemPrompt长度={}, userPrompt长度={}",
                modelKey, config.getModel(), systemPrompt.length(), userPrompt.length());

        ChatRequest request = new ChatRequest();
        request.setModel(config.getModel());
        request.setTemperature(config.getTemperature());
        request.setMaxTokens(config.getMaxTokens());
        request.setMessages(List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userPrompt)
        ));

        ChatResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("DeepSeek API 返回空响应");
        }

        String content = response.getChoices().get(0).getMessage().getContent();
        log.debug("【DeepSeek】响应长度={}", content.length());
        return content;
    }

    /**
     * 发送流式聊天请求（指定模型 key），返回 SSE 文本块流
     *
     * @param modelKey     模型标识
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return 流式文本块 Flux
     */
    public Flux<String> chatStream(String modelKey, String systemPrompt, String userPrompt) {
        DeepSeekProperties.ModelConfig config = properties.getModels().get(modelKey);
        if (config == null) {
            throw new IllegalArgumentException("未找到模型配置: " + modelKey);
        }

        log.debug("【DeepSeek】发送流式请求, modelKey={}, model={}", modelKey, config.getModel());

        return Flux.create(sink -> {
            try {
                StreamChatRequest request = new StreamChatRequest();
                request.setModel(config.getModel());
                request.setTemperature(config.getTemperature());
                request.setMaxTokens(config.getMaxTokens());
                request.setStream(true);
                request.setMessages(List.of(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", userPrompt)
                ));

                String bodyJson = objectMapper.writeValueAsString(request);

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.deepseek.com/chat/completions"))
                        .header("Authorization", "Bearer " + properties.getApiKey())
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                        .build();

                HttpResponse<InputStream> httpResponse = httpClient.send(
                        httpRequest, HttpResponse.BodyHandlers.ofInputStream());

                int statusCode = httpResponse.statusCode();
                if (statusCode != 200) {
                    String errorBody = new String(httpResponse.body().readAllBytes(), StandardCharsets.UTF_8);
                    log.error("【DeepSeek】流式请求失败, status={}, body={}", statusCode, errorBody);
                    sink.error(new RuntimeException("DeepSeek API 流式请求失败: HTTP " + statusCode + " - " + errorBody));
                    return;
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(httpResponse.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.isBlank()) continue;
                        if (!line.startsWith("data: ")) continue;

                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            break;
                        }

                        try {
                            StreamChunk chunk = objectMapper.readValue(data, StreamChunk.class);
                            if (chunk.getChoices() != null) {
                                for (StreamChoice choice : chunk.getChoices()) {
                                    if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
                                        sink.next(choice.getDelta().getContent());
                                    }
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.debug("【DeepSeek】跳过非JSON行: {}", line);
                        }
                    }
                }

                sink.complete();
                log.debug("【DeepSeek】流式响应完成, modelKey={}", modelKey);
            } catch (IOException e) {
                log.error("【DeepSeek】流式请求异常", e);
                sink.error(new RuntimeException("DeepSeek 流式请求失败: " + e.getMessage(), e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sink.error(new RuntimeException("DeepSeek 流式请求被中断", e));
            }
        });
    }

    // ========== 请求/响应模型 ==========

    @Data
    static class ChatRequest {
        private String model;
        private Double temperature;
        @JsonProperty("max_tokens")
        private Integer maxTokens = 4096;
        private List<ChatMessage> messages;
    }

    @Data
    static class StreamChatRequest {
        private String model;
        private Double temperature;
        @JsonProperty("max_tokens")
        private Integer maxTokens = 4096;
        private Boolean stream;
        private List<ChatMessage> messages;
    }

    @Data
    static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage() {}
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    static class ChatResponse {
        private List<Choice> choices;
    }

    @Data
    static class Choice {
        private ChatMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    static class StreamChunk {
        private List<StreamChoice> choices;
    }

    @Data
    static class StreamChoice {
        private Delta delta;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    static class Delta {
        private String content;
    }
}
