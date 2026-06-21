package com.exam.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 答案检查结果
 */
@Data
public class AnswerCheckResult {

    /** 题目ID */
    private Long questionId;

    /** 是否正确 */
    private Boolean isCorrect;

    /** 得分（0~题目满分） */
    private BigDecimal score;

    /** 满分 */
    private BigDecimal maxScore;

    /** AI评价/反馈 */
    private String feedback;

    /** 评分理由（AI解释为什么给这个分） */
    private String reason;
}
