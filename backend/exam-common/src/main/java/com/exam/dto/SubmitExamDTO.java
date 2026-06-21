package com.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SubmitExamDTO {
    @NotNull(message = "考试ID不能为空")
    private Long examId;

    @NotNull(message = "答题记录ID不能为空")
    private Long userExamId;

    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
    }
}
