package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_exam")
public class UserExam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long examId;
    private String answers;
    private BigDecimal score;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
