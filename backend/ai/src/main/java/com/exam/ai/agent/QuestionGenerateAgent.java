package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.exam.ai.config.TaskType;
import com.exam.ai.dto.AiQuestionResult;
import com.exam.ai.dto.GenerateQuestionRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试题生成Agent - 基于AI根据分类、题型、难度生成练习题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionGenerateAgent {

    private final DeepSeekClient deepSeekClient;
    private final ModelRouter modelRouter;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> TYPE_NAME_MAP = Map.of(
            "CHOICE", "选择题",
            "FILL", "填空题",
            "JUDGE", "判断题",
            "APPLICATION", "应用题"
    );

    private static final Map<Integer, String> DIFFICULTY_NAME_MAP = Map.of(
            1, "简单",
            2, "中等",
            3, "困难"
    );

    /**
     * 批量生成试题
     */
    public AiQuestionResult.BatchResult generateQuestions(GenerateQuestionRequest request) {
        String systemPrompt = buildSystemPrompt(request);
        String userPrompt = buildUserPrompt(request);

        log.info("【试题生成Agent】开始生成试题, category={}, types={}, count={}, difficulty={}",
                request.getCategoryName(), request.getTypes(), request.getCount(), request.getDifficulty());

        try {
            String modelKey = modelRouter.selectModel(TaskType.GENERATE_QUESTION);
            String response = deepSeekClient.chat(modelKey, systemPrompt, userPrompt);

            log.debug("【试题生成Agent】原始响应: {}", response);

            // 提取JSON部分
            String json = extractJson(response);
            List<Map<String, Object>> rawList = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});

            List<AiQuestionResult> questions = new ArrayList<>();
            for (Map<String, Object> raw : rawList) {
                questions.add(mapToQuestion(raw));
            }

            AiQuestionResult.BatchResult result = new AiQuestionResult.BatchResult();
            result.setQuestions(questions);
            result.setCategoryId(request.getCategoryId());
            result.setTotalCount(questions.size());

            log.info("【试题生成Agent】生成完成, 共{}道题", questions.size());
            return result;
        } catch (Exception e) {
            log.error("【试题生成Agent】生成失败", e);
            throw new RuntimeException("AI试题生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(GenerateQuestionRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位资深的考试出题专家。请根据以下要求生成高质量的练习题。\n\n");
        sb.append("## 题目规范\n");
        sb.append("1. 选择题(CHOICE)：4个选项，选项格式为JSON数组如[\"A.xxx\",\"B.xxx\",\"C.xxx\",\"D.xxx\"]，答案为选项字母如\"A\"\n");
        sb.append("2. 填空题(FILL)：留空处用___表示，答案为一个词或短语\n");
        sb.append("3. 判断题(JUDGE)：答案为「正确」或「错误」\n");
        sb.append("4. 应用题(APPLICATION)：需要计算或推理的综合性题目，答案为解题结果\n");
        sb.append("5. 题目必须严谨、准确，不能有歧义\n");
        sb.append("6. 每道题都要提供详细的解析\n\n");
        sb.append("## 输出格式\n");
        sb.append("请严格按照以下JSON数组格式输出，不要包含markdown代码块标记：\n");
        sb.append("[\n");
        sb.append("  {\n");
        sb.append("    \"type\": \"CHOICE\",\n");
        sb.append("    \"difficulty\": 2,\n");
        sb.append("    \"content\": \"题目内容\",\n");
        sb.append("    \"options\": \"[\\\"A.选项1\\\",\\\"B.选项2\\\",\\\"C.选项3\\\",\\\"D.选项4\\\"]\",\n");
        sb.append("    \"answer\": \"A\",\n");
        sb.append("    \"analysis\": \"详细解析\",\n");
        sb.append("    \"score\": 5.0\n");
        sb.append("  }\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(GenerateQuestionRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请生成").append(request.getCount()).append("道");

        // 分类/学科
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            sb.append("「").append(request.getCategoryName()).append("」相关的");
        }

        // 题型
        if (request.getTypes() != null && !request.getTypes().isEmpty()) {
            String typeNames = request.getTypes().stream()
                    .map(t -> TYPE_NAME_MAP.getOrDefault(t, t))
                    .collect(Collectors.joining("、"));
            sb.append(typeNames);
        } else {
            sb.append("练习题");
        }

        // 难度
        if (request.getDifficulty() != null) {
            sb.append("，难度为").append(DIFFICULTY_NAME_MAP.getOrDefault(request.getDifficulty(), "中等"));
        }

        // 主题/知识点
        if (request.getTopic() != null && !request.getTopic().isBlank()) {
            sb.append("，考察知识点：「").append(request.getTopic()).append("」");
        }

        sb.append("。");

        // 题型具体要求
        if (request.getTypes() != null && request.getTypes().size() == 1) {
            String type = request.getTypes().get(0);
            if ("CHOICE".equals(type)) {
                sb.append("请确保每个选项长度相近，干扰项要有迷惑性。");
            } else if ("APPLICATION".equals(type)) {
                sb.append("请结合实际应用场景出题，考察综合运用能力。");
            }
        }

        return sb.toString();
    }

    /**
     * 从AI响应中提取JSON数组
     */
    private String extractJson(String response) {
        String trimmed = response.trim();
        // 去除可能的markdown代码块标记
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf("\n");
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) {
                trimmed = trimmed.substring(start, end).trim();
            }
        }
        // 确保以[开头
        int arrayStart = trimmed.indexOf('[');
        int arrayEnd = trimmed.lastIndexOf(']');
        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            return trimmed.substring(arrayStart, arrayEnd + 1);
        }
        return trimmed;
    }

    /**
     * Map转AiQuestionResult
     */
    private AiQuestionResult mapToQuestion(Map<String, Object> map) {
        AiQuestionResult q = new AiQuestionResult();
        q.setType(getString(map, "type", "CHOICE"));
        q.setDifficulty(getInt(map, "difficulty", 2));
        q.setContent(getString(map, "content", ""));
        q.setOptions(getString(map, "options", null));
        q.setAnswer(getString(map, "answer", ""));
        q.setAnalysis(getString(map, "analysis", ""));
        Object scoreObj = map.get("score");
        if (scoreObj instanceof Number) {
            q.setScore(BigDecimal.valueOf(((Number) scoreObj).doubleValue()));
        } else if (scoreObj instanceof String) {
            try {
                q.setScore(new BigDecimal((String) scoreObj));
            } catch (Exception e) {
                q.setScore(BigDecimal.valueOf(5.0));
            }
        } else {
            q.setScore(BigDecimal.valueOf(5.0));
        }
        return q;
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private Integer getInt(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
