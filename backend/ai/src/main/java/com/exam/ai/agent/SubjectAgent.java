package com.exam.ai.agent;

import com.exam.ai.dto.*;
import reactor.core.publisher.Flux;

/**
 * 科目专属Agent接口 - 每个科目有独立的试题生成、答案检查、试题讲解策略
 */
public interface SubjectAgent {

    /**
     * 科目名称（如"语文""数学""英语""物理""化学"）
     */
    String getSubjectName();

    /**
     * 该科目支持的题型列表
     */
    String[] getQuestionTypes();

    /**
     * 生成练习题（科目专属提示词）
     */
    AiQuestionResult.BatchResult generateQuestions(GenerateQuestionRequest request);

    /**
     * 检查答案（科目专属判分标准）
     */
    AnswerCheckResult checkAnswer(CheckAnswerRequest request);

    /**
     * 试题讲解（科目专属讲解模板）
     */
    ExplanationResult explainQuestion(ExplainQuestionRequest request);

    /**
     * 流式检查答案 —— 返回 SSE 文本块流
     */
    Flux<String> checkAnswerStream(CheckAnswerRequest request);

    /**
     * 流式试题讲解 —— 返回 SSE 文本块流
     */
    Flux<String> explainQuestionStream(ExplainQuestionRequest request);
}
