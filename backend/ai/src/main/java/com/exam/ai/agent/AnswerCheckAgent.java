package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.exam.ai.config.TaskType;
import com.exam.ai.dto.AnswerCheckResult;
import com.exam.ai.dto.CheckAnswerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 答案检查Agent - 智能判题，特别适合应用题等需要AI主观评判的题型
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnswerCheckAgent {

    private final DeepSeekClient deepSeekClient;
    private final ModelRouter modelRouter;
    private final ObjectMapper objectMapper;

    /**
     * 检查用户答案
     */
    public AnswerCheckResult checkAnswer(CheckAnswerRequest request) {
        log.info("【答案检查Agent】开始判题, questionId={}, type={}", request.getQuestionId(), request.getQuestionType());

        String prompt = buildCheckPrompt(request);

        try {
            String modelKey = modelRouter.selectModel(TaskType.CHECK_ANSWER);
            String response = deepSeekClient.chat(modelKey, buildSystemPrompt(), prompt);

            log.debug("【答案检查Agent】原始响应: {}", response);

            String json = extractJson(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);

            AnswerCheckResult result = new AnswerCheckResult();
            result.setQuestionId(request.getQuestionId());
            result.setIsCorrect(Boolean.TRUE.equals(map.get("isCorrect")) || "true".equalsIgnoreCase(String.valueOf(map.get("isCorrect"))));
            result.setFeedback(String.valueOf(map.getOrDefault("feedback", "")));
            result.setReason(String.valueOf(map.getOrDefault("reason", "")));

            // 分数
            Object scoreObj = map.get("score");
            if (scoreObj instanceof Number) {
                result.setScore(BigDecimal.valueOf(((Number) scoreObj).doubleValue()));
            } else if (scoreObj instanceof String) {
                try {
                    result.setScore(new BigDecimal((String) scoreObj));
                } catch (Exception e) {
                    result.setScore(BigDecimal.ZERO);
                }
            } else {
                result.setScore(result.getIsCorrect() ? BigDecimal.valueOf(5) : BigDecimal.ZERO);
            }

            log.info("【答案检查Agent】判题完成, isCorrect={}, score={}", result.getIsCorrect(), result.getScore());
            return result;
        } catch (Exception e) {
            log.error("【答案检查Agent】判题失败", e);
            throw new RuntimeException("AI答案检查失败: " + e.getMessage(), e);
        }
    }

    private String buildSystemPrompt() {
        return """
                你是一位严谨的阅卷老师。请根据题目信息、标准答案和用户答案，判断用户答案是否正确并给出评分和反馈。

                ## 判分规则
                1. 选择题(CHOICE)：用户答案与标准答案完全一致即正确，给满分；否则0分
                2. 判断题(JUDGE)：用户答案与标准答案语义一致即正确（如"对"/"正确"/"√"等价），给满分；否则0分
                3. 填空题(FILL)：用户答案与标准答案关键信息匹配即正确（可忽略大小写和标点差异），给满分；部分正确给一半分；完全不正确0分
                4. 应用题(APPLICATION)：需综合评估解题思路、步骤和最终结果：
                   - 过程和结果完全正确：给满分
                   - 思路正确但计算错误：给70%分数
                   - 部分正确：酌情给30%-50%分数
                   - 完全错误：0分

                ## 输出格式
                请严格按以下JSON格式输出，不要包含markdown代码块标记：
                {
                  "isCorrect": true/false,
                  "score": 5.0,
                  "feedback": "简短评语，如'回答正确！'或'答案有误'",
                  "reason": "详细评分理由"
                }
                """;
    }

    private String buildCheckPrompt(CheckAnswerRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请评判以下用户答案：\n\n");
        sb.append("【题目类型】").append(getTypeName(request.getQuestionType())).append("\n");
        sb.append("【题目内容】").append(request.getQuestionContent()).append("\n");

        if (request.getQuestionOptions() != null && !request.getQuestionOptions().isBlank()) {
            sb.append("【题目选项】").append(request.getQuestionOptions()).append("\n");
        }

        sb.append("【标准答案】").append(request.getCorrectAnswer()).append("\n");
        sb.append("【用户答案】").append(request.getUserAnswer()).append("\n");

        if (request.getAnalysis() != null && !request.getAnalysis().isBlank()) {
            sb.append("【参考解析】").append(request.getAnalysis()).append("\n");
        }

        return sb.toString();
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "CHOICE" -> "选择题";
            case "FILL" -> "填空题";
            case "JUDGE" -> "判断题";
            case "APPLICATION" -> "应用题";
            default -> type;
        };
    }

    /**
     * 流式检查用户答案 —— 返回 SSE 文本块流
     */
    public Flux<String> checkAnswerStream(CheckAnswerRequest request) {
        log.info("【答案检查Agent】开始流式判题, questionId={}, type={}", request.getQuestionId(), request.getQuestionType());

        String prompt = buildCheckPrompt(request);

        try {
            String modelKey = modelRouter.selectModel(TaskType.CHECK_ANSWER);
            return deepSeekClient.chatStream(modelKey, buildSystemPrompt(), prompt);
        } catch (Exception e) {
            log.error("【答案检查Agent】流式判题启动失败", e);
            return Flux.error(new RuntimeException("AI流式答案检查失败: " + e.getMessage(), e));
        }
    }

    private String extractJson(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf("\n");
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) {
                trimmed = trimmed.substring(start, end).trim();
            }
        }
        int objStart = trimmed.indexOf('{');
        int objEnd = trimmed.lastIndexOf('}');
        if (objStart >= 0 && objEnd > objStart) {
            return trimmed.substring(objStart, objEnd + 1);
        }
        return trimmed;
    }
}
