package com.exam.ai.dto;

import lombok.Data;

/**
 * 试题讲解结果
 */
@Data
public class ExplanationResult {

    /** 题目ID */
    private Long questionId;

    /** 题目分析（考点、知识点） */
    private String analysis;

    /** 解题思路 */
    private String approach;

    /** 详细步骤 */
    private String steps;

    /** 完整讲解内容 */
    private String fullExplanation;

    /** 知识点总结/易错提醒 */
    private String summary;
}
