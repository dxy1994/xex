package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String name;
    private String description;
    private String imageUrl;
    private Integer sortOrder;

    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 子分类列表（非数据库字段，用于树结构返回） */
    @TableField(exist = false)
    private List<Category> children;

    /** 学段标签（非数据库字段，63学制：一年级~六=小学/七~九=初中；54学制：一~五=小学/六~九=初中） */
    @TableField(exist = false)
    private String stageLabel;
}
