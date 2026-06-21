package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.exam.ai.config.TaskType;
import com.exam.ai.dto.ExplainQuestionRequest;
import com.exam.ai.dto.ExplanationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 试题讲解Agent - 为试题提供详细的解题思路、步骤和知识点讲解
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionExplainAgent {

    private final DeepSeekClient deepSeekClient;
    private final ModelRouter modelRouter;
    private final ObjectMapper objectMapper;

    /**
     * 讲解试题
     */
    public ExplanationResult explainQuestion(ExplainQuestionRequest request) {
        log.info("【试题讲解Agent】开始讲解, questionId={}, type={}, detailLevel={}",
                request.getQuestionId(), request.getQuestionType(), request.getDetailLevel());

        String prompt = buildExplainPrompt(request);

        try {
            String modelKey = modelRouter.selectModel(TaskType.EXPLAIN_QUESTION);
            String response = deepSeekClient.chat(modelKey,
                    buildSystemPrompt(request.getDetailLevel()), prompt);

            log.debug("【试题讲解Agent】原始响应: {}", response);

            String json = extractJson(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);

            ExplanationResult result = new ExplanationResult();
            result.setQuestionId(request.getQuestionId());
            result.setAnalysis(String.valueOf(map.getOrDefault("analysis", "")));
            result.setApproach(String.valueOf(map.getOrDefault("approach", "")));
            result.setSteps(String.valueOf(map.getOrDefault("steps", "")));

            // 构建完整讲解内容
            StringBuilder fullExplanation = new StringBuilder();
            if (!result.getAnalysis().isEmpty()) {
                fullExplanation.append("【考点分析】\n").append(result.getAnalysis()).append("\n\n");
            }
            if (!result.getApproach().isEmpty()) {
                fullExplanation.append("【解题思路】\n").append(result.getApproach()).append("\n\n");
            }
            if (!result.getSteps().isEmpty()) {
                fullExplanation.append("【详细步骤】\n").append(result.getSteps()).append("\n\n");
            }
            String summary = String.valueOf(map.getOrDefault("summary", ""));
            if (!summary.isEmpty()) {
                fullExplanation.append("【知识点总结】\n").append(summary);
            }
            result.setFullExplanation(fullExplanation.toString().trim());
            result.setSummary(summary);

            log.info("【试题讲解Agent】讲解完成, questionId={}", request.getQuestionId());
            return result;
        } catch (Exception e) {
            log.error("【试题讲解Agent】讲解失败", e);
            throw new RuntimeException("AI试题讲解失败: " + e.getMessage(), e);
        }
    }

    private String buildSystemPrompt(String detailLevel) {
        String levelInstruction = switch (detailLevel) {
            case "brief" -> "请给出简要的分析和结论，控制在200字以内。";
            case "step-by-step" -> "请给出逐步的详细推导，每一步都要解释清楚，适合初学者理解。";
            default -> "请给出详细的分析和解答过程。";
        };

        return """
                你是一位经验丰富的教师，擅长为学生讲解试题。请根据题目信息给出专业的讲解。

                ## 讲解要求
                1. 先分析题目考察的知识点和考点
                2. 给出解题思路和方法
                3. 逐步推导出正确答案
                4. 总结易错点和注意事项
                5. 语言要通俗易懂，适合学生理解
                %s

                ## 输出格式
                请严格按以下JSON格式输出，不要包含markdown代码块标记：
                {
                  "analysis": "考点分析：这道题主要考察...",
                  "approach": "解题思路：我们可以通过...方法来解答",
                  "steps": "解题步骤：\\n1. 首先...\\n2. 然后...\\n3. 最后...",
                  "summary": "知识点总结：本题涉及...，需要注意..."
                }
                """.formatted(levelInstruction);
    }

    private String buildExplainPrompt(ExplainQuestionRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请讲解以下试题：\n\n");
        sb.append("【题目类型】").append(getTypeName(request.getQuestionType())).append("\n");
        sb.append("【题目内容】").append(request.getQuestionContent()).append("\n");

        if (request.getQuestionOptions() != null && !request.getQuestionOptions().isBlank()) {
            sb.append("【题目选项】").append(request.getQuestionOptions()).append("\n");
        }

        sb.append("【正确答案】").append(request.getCorrectAnswer()).append("\n");

        if (request.getExistingAnalysis() != null && !request.getExistingAnalysis().isBlank()) {
            sb.append("【已有解析（参考）】").append(request.getExistingAnalysis()).append("\n");
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
     * 流式讲解试题 —— 返回 SSE 文本块流
     */
    public Flux<String> explainQuestionStream(ExplainQuestionRequest request) {
        log.info("【试题讲解Agent】开始流式讲解, questionId={}, type={}, detailLevel={}",
                request.getQuestionId(), request.getQuestionType(), request.getDetailLevel());

        String prompt = buildExplainPrompt(request);

        try {
            String modelKey = modelRouter.selectModel(TaskType.EXPLAIN_QUESTION);
            return deepSeekClient.chatStream(modelKey,
                    buildSystemPrompt(request.getDetailLevel()), prompt);
        } catch (Exception e) {
            log.error("【试题讲解Agent】流式讲解启动失败", e);
            return Flux.error(new RuntimeException("AI流式试题讲解失败: " + e.getMessage(), e));
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
