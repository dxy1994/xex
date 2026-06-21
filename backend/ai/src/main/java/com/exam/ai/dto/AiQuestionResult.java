package com.exam.ai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI生成的试题结果
 */
@Data
public class AiQuestionResult {

    /** 题目类型：CHOICE/FILL/JUDGE/APPLICATION */
    private String type;

    /** 难度：1-简单 2-中等 3-困难 */
    private Integer difficulty;

    /** 题目内容 */
    private String content;

    /** 选项（JSON数组字符串，选择题用） */
    private String options;

    /** 正确答案 */
    private String answer;

    /** 解析 */
    private String analysis;

    /** 建议分值 */
    private BigDecimal score;

    /** AI批量生成结果包装 */
    @Data
    public static class BatchResult {
        private List<AiQuestionResult> questions;
        private Long categoryId;
        private Integer totalCount;
    }
}
