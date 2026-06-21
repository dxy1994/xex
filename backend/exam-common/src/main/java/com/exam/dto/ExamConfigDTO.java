package com.exam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamConfigDTO {
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Min(value = 1, message = "题目数量至少为1")
    private Integer totalQuestions;

    private Integer duration;

    /** 种子号：相同种子+相同配置可重现相同试卷，为空则自动生成 */
    private String seed;
}
