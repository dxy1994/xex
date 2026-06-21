package com.exam.ai.config;

/**
 * AI 任务类型枚举 —— 用于 ModelRouter 根据任务复杂度选择模型
 */
public enum TaskType {

    /** 试题生成 —— 需要创造力 + 知识点覆盖 + 题型设计，使用推理模型 */
    GENERATE_QUESTION,

    /** 答案检查 —— 对比判断，无需深度推理，使用快速模型 */
    CHECK_ANSWER,

    /** 试题讲解 —— 需要深度分析解题思路和知识点，使用推理模型 */
    EXPLAIN_QUESTION

}
