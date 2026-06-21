package com.exam.ai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 试题生成请求
 */
@Data
public class GenerateQuestionRequest {

    /** 分类ID */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /** 分类名称（用于AI理解上下文） */
    private String categoryName;

    /** 题目类型：CHOICE/FILL/JUDGE/APPLICATION，为空则按比例生成 */
    private List<String> types;

    /** 难度：1-简单 2-中等 3-困难 */
    @Min(value = 1, message = "难度最小为1")
    private Integer difficulty;

    /** 生成数量，默认5 */
    @Min(value = 1, message = "生成数量至少为1")
    private Integer count = 5;

    /** 知识点/主题描述（可选，用于定向出题） */
    private String topic;
}
