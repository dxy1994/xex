package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.exam.ai.config.TaskType;
import com.exam.ai.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 科目Agent抽象基类 —— 提供题型感知的提示词构建、JSON解析、判分讲解公共能力。
 * <p>
 * 子类只需实现 {@link #getSubjectSpecificGenerateGuide()}, {@link #getSubjectSpecificCheckRules()},
 * {@link #getSubjectSpecificExplainGuide()} 三个方法来注入学科差异化规则。
 */
@Slf4j
public abstract class BaseSubjectAgent implements SubjectAgent {

    protected final DeepSeekClient deepSeekClient;
    protected final ModelRouter modelRouter;
    protected final ObjectMapper objectMapper;

    protected BaseSubjectAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.modelRouter = modelRouter;
        this.objectMapper = objectMapper;
    }

    // ═══════════════════════════════════════════════════════
    // 对外开放的三大核心方法
    // ═══════════════════════════════════════════════════════

    @Override
    public AiQuestionResult.BatchResult generateQuestions(GenerateQuestionRequest request) {
        log.info("【{}Agent】开始生成试题, category={}, count={}, difficulty={}",
                getSubjectName(), request.getCategoryName(), request.getCount(), request.getDifficulty());

        try {
            String modelKey = modelRouter.selectModel(TaskType.GENERATE_QUESTION);
            String response = deepSeekClient.chat(modelKey, buildGenerateSystemPrompt(request), buildGenerateUserPrompt(request));
            log.debug("【{}Agent】生成原始响应: {}", getSubjectName(), response);

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

            log.info("【{}Agent】生成完成, 共{}道题", getSubjectName(), questions.size());
            return result;
        } catch (Exception e) {
            log.error("【{}Agent】生成失败", getSubjectName(), e);
            throw new RuntimeException(getSubjectName() + "试题生成失败: " + e.getMessage(), e);
        }
    }

    @Override
    public AnswerCheckResult checkAnswer(CheckAnswerRequest request) {
        log.info("【{}Agent】开始判题, questionId={}", getSubjectName(), request.getQuestionId());

        try {
            String modelKey = modelRouter.selectModel(TaskType.CHECK_ANSWER);
            String response = deepSeekClient.chat(modelKey, buildCheckSystemPrompt(), buildCheckUserPrompt(request));
            log.debug("【{}Agent】判题原始响应: {}", getSubjectName(), response);

            String json = extractJson(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);

            AnswerCheckResult result = new AnswerCheckResult();
            result.setQuestionId(request.getQuestionId());
            result.setIsCorrect(parseBoolean(map.get("isCorrect")));
            result.setFeedback(String.valueOf(map.getOrDefault("feedback", "")));
            result.setReason(String.valueOf(map.getOrDefault("reason", "")));
            result.setScore(parseScore(map.get("score")));

            log.info("【{}Agent】判题完成, isCorrect={}, score={}", getSubjectName(), result.getIsCorrect(), result.getScore());
            return result;
        } catch (Exception e) {
            log.error("【{}Agent】判题失败", getSubjectName(), e);
            throw new RuntimeException(getSubjectName() + "答案检查失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ExplanationResult explainQuestion(ExplainQuestionRequest request) {
        log.info("【{}Agent】开始讲解, questionId={}", getSubjectName(), request.getQuestionId());

        try {
            String modelKey = modelRouter.selectModel(TaskType.EXPLAIN_QUESTION);
            String response = deepSeekClient.chat(modelKey, buildExplainSystemPrompt(request.getDetailLevel()), buildExplainUserPrompt(request));
            log.debug("【{}Agent】讲解原始响应: {}", getSubjectName(), response);

            String json = extractJson(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);

            ExplanationResult result = new ExplanationResult();
            result.setQuestionId(request.getQuestionId());
            result.setAnalysis(String.valueOf(map.getOrDefault("analysis", "")));
            result.setApproach(String.valueOf(map.getOrDefault("approach", "")));
            result.setSteps(String.valueOf(map.getOrDefault("steps", "")));
            result.setSummary(String.valueOf(map.getOrDefault("summary", "")));

            StringBuilder full = new StringBuilder();
            if (!result.getAnalysis().isEmpty()) full.append("【考点分析】\n").append(result.getAnalysis()).append("\n\n");
            if (!result.getApproach().isEmpty()) full.append("【解题思路】\n").append(result.getApproach()).append("\n\n");
            if (!result.getSteps().isEmpty()) full.append("【详细步骤】\n").append(result.getSteps()).append("\n\n");
            if (!result.getSummary().isEmpty()) full.append("【知识点总结】\n").append(result.getSummary());
            result.setFullExplanation(full.toString().trim());

            log.info("【{}Agent】讲解完成, questionId={}", getSubjectName(), request.getQuestionId());
            return result;
        } catch (Exception e) {
            log.error("【{}Agent】讲解失败", getSubjectName(), e);
            throw new RuntimeException(getSubjectName() + "试题讲解失败: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    // 流式输出方法
    // ═══════════════════════════════════════════════════════

    @Override
    public Flux<String> checkAnswerStream(CheckAnswerRequest request) {
        log.info("【{}Agent】开始流式判题, questionId={}", getSubjectName(), request.getQuestionId());
        try {
            String modelKey = modelRouter.selectModel(TaskType.CHECK_ANSWER);
            return deepSeekClient.chatStream(modelKey, buildCheckSystemPrompt(), buildCheckUserPrompt(request));
        } catch (Exception e) {
            log.error("【{}Agent】流式判题启动失败", getSubjectName(), e);
            return Flux.error(new RuntimeException(getSubjectName() + "流式答案检查失败: " + e.getMessage(), e));
        }
    }

    @Override
    public Flux<String> explainQuestionStream(ExplainQuestionRequest request) {
        log.info("【{}Agent】开始流式讲解, questionId={}", getSubjectName(), request.getQuestionId());
        try {
            String modelKey = modelRouter.selectModel(TaskType.EXPLAIN_QUESTION);
            return deepSeekClient.chatStream(modelKey,
                    buildExplainSystemPrompt(request.getDetailLevel()), buildExplainUserPrompt(request));
        } catch (Exception e) {
            log.error("【{}Agent】流式讲解启动失败", getSubjectName(), e);
            return Flux.error(new RuntimeException(getSubjectName() + "流式试题讲解失败: " + e.getMessage(), e));
        }
    }

    // ═══════════════════════════════════════════════════════
    // 子类需实现的三个抽象方法（学科差异化注入点）
    // ═══════════════════════════════════════════════════════

    /**
     * 学科生成试题的专属指导（角色定位、出题原则、题型偏好、分值建议等）
     */
    protected abstract String getSubjectSpecificGenerateGuide();

    /**
     * 学科判分的专属规则（给分/扣分细则、特殊容错、等价判断等）
     */
    protected abstract String getSubjectSpecificCheckRules();

    /**
     * 学科讲解的专属风格（讲解深度、侧重方向、例题引用等）
     */
    protected abstract String getSubjectSpecificExplainGuide();

    // ═══════════════════════════════════════════════════════
    // 提示词构建（自动化题型感知 + 学科专属注入）
    // ═══════════════════════════════════════════════════════

    /** 构建试题生成系统提示词 */
    protected String buildGenerateSystemPrompt(GenerateQuestionRequest request) {
        return """
                你是一位专业的%s命题专家，精通各类考试题型的设计与命题规范。
                
                【你的任务】
                根据用户指定的知识点和难度要求，生成高质量、符合考试规范的试题。
                
                【学科要求】
                %s
                
                【可选题型及格式规范】
                下面是你需要掌握的试题题型，每种题型有明确的格式要求：
                
                %s
                
                【出题原则】
                1. 知识点覆盖准确，难度与学段匹配
                2. 题目表述清晰无歧义，选项具有迷惑性但不刻意刁难
                3. 选择题的干扰项应源于常见错误理解
                4. 填空题空白位置合理，答案唯一或等价可判别
                5. 计算题/证明题需给出完整解题过程作为答案
                6. 作文/论述题需给出详细的评分标准和得分要点
                7. 实验题需描述实验背景、器材和预期现象
                8. 多选题应明确标注"多选"，且正确答案≥2个
                9. 不定项选择题正确答案可能为1个或多个
                
                %s
                """.formatted(
                getSubjectName(),
                getSubjectSpecificGenerateGuide(),
                buildTypeFormatGuide(),
                outputJsonArrayFormat());
    }

    /** 构建试题生成用户提示词 */
    protected String buildGenerateUserPrompt(GenerateQuestionRequest request) {
        String[] types = getQuestionTypes();
        String typeList = String.join("、", types);
        return """
                请生成%d道%s试题：
                
                知识点分类：%s
                难度等级：%d（1-简单 2-中等 3-困难）
                可用题型：%s
                
                要求：
                1. 尽量使用不同的题型，避免全部都是同一种题型
                2. 严格控制难度：简单题注重基础概念，中等题考查理解运用，困难题考查综合分析
                3. 每题给出完整的解析（analysis字段），说明解题思路和知识点
                4. 分值默认5分，可根据题型复杂度适当调整
                """.formatted(
                request.getCount(), getSubjectName(),
                request.getCategoryName(), request.getDifficulty(), typeList);
    }

    /** 构建答案检查系统提示词 */
    protected String buildCheckSystemPrompt() {
        return """
                你是一位严谨的%s阅卷教师，需要根据标准答案评判学生的作答情况。
                
                【判分原则】
                1. 严格对照标准答案，但允许合理的表述差异
                2. 根据题型采用不同的判分策略（详见下方规则）
                3. 部分正确的答案应酌情给分，而非一刀切
                
                【题型判分规则】
                - 单选/判断题：答案完全匹配才得分，否则0分
                - 多选题：全部选对得满分，漏选得一半分，错选得0分
                - 不定项选：按正确选项比例给分
                - 填空题：语义等价即可，关键信息正确即得分
                - 简答题/名词解释：按要点覆盖比例给分，表述不同但意思正确应给分
                - 计算题：最终结果正确+过程合理 → 满分；结果错误但过程正确 → 60%%~80%%分
                - 证明题/推导题：逻辑链完整即给分，小错误扣少量分
                - 实验题：原理正确、步骤合理即给分
                - 作文/论述题：综合内容、结构、语言给分
                - 翻译题：关键字翻译正确即可得分
                
                【学科专属判分规则】
                %s
                
                %s
                """.formatted(getSubjectName(), getSubjectSpecificCheckRules(), outputCheckJsonFormat());
    }

    /** 构建答案检查用户提示词 */
    protected String buildCheckUserPrompt(CheckAnswerRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("【题目类型】").append(request.getQuestionType()).append("\n");
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

    /** 构建试题讲解系统提示词 */
    protected String buildExplainSystemPrompt(String detailLevel) {
        String depth = switch (detailLevel != null ? detailLevel : "full") {
            case "brief" -> "给出简洁的考点提示和关键步骤，控制在200字以内";
            case "detailed" -> "深入剖析每个知识点，给出完整解题过程，可补充同类题型的解题技巧";
            default -> "给出完整的考点分析、解题思路、详细步骤和知识点总结";
        };
        return """
                你是一位优秀的%s教师，擅长用通俗易懂的方式讲解试题。
                
                【讲解要求】
                你将收到一道%s试题及其答案，请按以下结构进行讲解：
                
                1. 考点分析（analysis）：指出题目考查的知识点和能力要求
                2. 解题思路（approach）：说明解题的切入点和思维路径
                3. 解题步骤（steps）：逐步展示解题过程
                4. 知识点总结（summary）：提炼涉及的知识点，并给出易错提醒
                
                【深度要求】
                %s
                
                【学科讲解风格】
                %s
                
                %s
                """.formatted(
                getSubjectName(), getSubjectName(),
                depth,
                getSubjectSpecificExplainGuide(),
                outputExplainJsonFormat());
    }

    /** 构建试题讲解用户提示词 */
    protected String buildExplainUserPrompt(ExplainQuestionRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请讲解以下").append(getSubjectName()).append("试题：\n\n");
        sb.append("【题目类型】").append(request.getQuestionType()).append("\n");
        sb.append("【题目内容】").append(request.getQuestionContent()).append("\n");
        if (request.getQuestionOptions() != null && !request.getQuestionOptions().isBlank()) {
            sb.append("【题目选项】").append(request.getQuestionOptions()).append("\n");
        }
        sb.append("【正确答案】").append(request.getCorrectAnswer()).append("\n");
        if (request.getExistingAnalysis() != null && !request.getExistingAnalysis().isBlank()) {
            sb.append("【已有解析】").append(request.getExistingAnalysis()).append("\n");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════
    // 格式说明构建
    // ═══════════════════════════════════════════════════════

    /**
     * 根据科目支持的题型列表，生成题型→格式的对照说明
     */
    private String buildTypeFormatGuide() {
        StringBuilder sb = new StringBuilder();
        for (String code : getQuestionTypes()) {
            QuestionType qt = QuestionType.fromCode(code);
            sb.append("■ ").append(qt.getLabel()).append("（").append(code).append("）\n");
            sb.append("  ").append(qt.getFormatGuide()).append("\n\n");
        }
        // 去重（因为 fromCode 可能把多个 code 映射到同一个枚举）
        return sb.toString();
    }

    /** 输出格式说明（题型感知版本） */
    protected String outputJsonArrayFormat() {
        return """
                【输出格式】
                请严格按以下JSON数组格式输出，不要包含markdown代码块标记：
                [
                  {
                    "type": "题目类型代码（如SINGLE_CHOICE/MULTI_CHOICE/FILL_BLANK/CALCULATION等）",
                    "difficulty": 2,
                    "content": "题目完整内容（含材料、图表描述等）",
                    "options": "选项JSON数组字符串，如[\\"A.xxx\\",\\"B.xxx\\",\\"C.xxx\\",\\"D.xxx\\"]；无选项题型为null",
                    "answer": "正确答案（选择题为选项字母，填空为填充内容，计算题为过程+结果，作文为评分标准）",
                    "analysis": "详细解题思路和知识点解析",
                    "score": 5.0
                  }
                ]
                
                注意：
                - type字段必须使用上面列出的英文代码
                - options字段是JSON字符串（非对象），无选项时设为null
                - answer字段：选择题填选项字母(如"A"或"ABD")，判断题填"正确"/"错误"，其他题型填完整答案
                - 计算题/证明题的answer应包含完整解题过程
                - 作文/论述题的answer应包含评分标准和得分要点""";
    }

    /** 判分输出格式 */
    protected String outputCheckJsonFormat() {
        return """
                【输出格式】
                请严格按以下JSON格式输出：
                {
                  "isCorrect": true/false,
                  "score": 5.0,
                  "feedback": "简短评语（如'回答正确'/'部分正确，需补充…'/'错误，正确思路是…'）",
                  "reason": "详细评分理由（如：第1/3点正确得分，第2点遗漏扣2分…）"
                }
                
                注意：
                - score是实际得分（含小数），不超过题目原分值
                - isCorrect根据得分是否≥满分的60%%判断
                - feedback面向学生，语气鼓励、具体
                - reason面向教师，说明扣分/给分依据""";
    }

    /** 讲解输出格式 */
    protected String outputExplainJsonFormat() {
        return """
                【输出格式】
                请严格按以下JSON格式输出：
                {
                  "analysis": "【考点分析】指出考查的知识点、能力层级、常见错误类型",
                  "approach": "【解题思路】从审题到找到突破口的思维路径，解题策略选择依据",
                  "steps": "【详细步骤】逐步拆解解题过程，每一步配说明",
                  "summary": "【知识点总结】提炼核心知识点，给出同类题型的解题模板和易错提醒"
                }""";
    }

    // ═══════════════════════════════════════════════════════
    // JSON工具方法
    // ═══════════════════════════════════════════════════════

    protected String extractJson(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf("\n");
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) trimmed = trimmed.substring(start, end).trim();
        }
        if (trimmed.startsWith("[")) {
            int end = trimmed.lastIndexOf(']');
            if (end > 0) return trimmed.substring(0, end + 1);
        }
        if (trimmed.startsWith("{")) {
            int end = trimmed.lastIndexOf('}');
            if (end > 0) return trimmed.substring(0, end + 1);
        }
        return trimmed;
    }

    protected AiQuestionResult mapToQuestion(Map<String, Object> map) {
        AiQuestionResult q = new AiQuestionResult();
        q.setType(getString(map, "type", "SINGLE_CHOICE"));
        q.setDifficulty(getInt(map, "difficulty", 2));
        q.setContent(getString(map, "content", ""));
        q.setOptions(getString(map, "options", null));
        q.setAnswer(getString(map, "answer", ""));
        q.setAnalysis(getString(map, "analysis", ""));
        q.setScore(parseScore(map.get("score")));
        return q;
    }

    protected String getString(Map<String, Object> map, String key, String defaultValue) {
        Object v = map.get(key);
        return v != null ? v.toString() : defaultValue;
    }

    protected Integer getInt(Map<String, Object> map, String key, Integer defaultValue) {
        Object v = map.get(key);
        return v instanceof Number n ? n.intValue() : defaultValue;
    }

    protected boolean parseBoolean(Object obj) {
        if (obj instanceof Boolean b) return b;
        if (obj instanceof String s) return "true".equalsIgnoreCase(s);
        return false;
    }

    protected BigDecimal parseScore(Object obj) {
        if (obj instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        if (obj instanceof String s) {
            try { return new BigDecimal(s); } catch (Exception e) { /* fall through */ }
        }
        return BigDecimal.valueOf(5);
    }
}
