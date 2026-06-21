package com.exam.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 试题讲解请求
 */
@Data
public class ExplainQuestionRequest {

    /** 题目ID */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /** 题目类型 */
    @NotBlank(message = "题目类型不能为空")
    private String questionType;

    /** 题目内容 */
    @NotBlank(message = "题目内容不能为空")
    private String questionContent;

    /** 题目选项（选择题/判断题有，JSON格式） */
    private String questionOptions;

    /** 正确答案 */
    @NotBlank(message = "正确答案不能为空")
    private String correctAnswer;

    /** 已有解析（可空，AI会增强或重写） */
    private String existingAnalysis;

    /** 讲解深度：brief-简要 detailed-详细 step-by-step-逐步 */
    private String detailLevel = "detailed";

    /** 科目名称（可选，如"语文""数学"，用于路由到对应科目Agent） */
    private String subjectName;
}
