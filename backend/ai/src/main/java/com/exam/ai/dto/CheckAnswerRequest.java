package com.exam.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 答案检查请求
 */
@Data
public class CheckAnswerRequest {

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

    /** 标准答案 */
    @NotBlank(message = "标准答案不能为空")
    private String correctAnswer;

    /** 用户答案 */
    @NotBlank(message = "用户答案不能为空")
    private String userAnswer;

    /** 题目解析（用于AI参考） */
    private String analysis;

    /** 科目名称（可选，如"语文""数学"，用于路由到对应科目Agent） */
    private String subjectName;
}
