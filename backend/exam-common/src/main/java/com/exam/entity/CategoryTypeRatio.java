package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@TableName("category_type_ratio")
public class CategoryTypeRatio {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String typeRatios;
    private String difficultyRatios;

    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Map<String, Integer> getTypeRatioMap() {
        return parseJsonToIntMap(typeRatios);
    }

    public void setTypeRatioMap(Map<String, Integer> map) {
        this.typeRatios = toJson(map);
    }

    public Map<String, Integer> getDifficultyRatioMap() {
        return parseJsonToIntMap(difficultyRatios);
    }

    public void setDifficultyRatioMap(Map<String, Integer> map) {
        this.difficultyRatios = toJson(map);
    }

    private Map<String, Integer> parseJsonToIntMap(String json) {
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private String toJson(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) return "{}";
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    public static CategoryTypeRatio createDefault(Long categoryId, String[] questionTypes) {
        CategoryTypeRatio ratio = new CategoryTypeRatio();
        ratio.setCategoryId(categoryId);
        Map<String, Integer> typeMap = new LinkedHashMap<>();
        int perType = 100 / questionTypes.length;
        int remainder = 100 - perType * questionTypes.length;
        for (int i = 0; i < questionTypes.length; i++) {
            typeMap.put(questionTypes[i], perType + (i == 0 ? remainder : 0));
        }
        ratio.setTypeRatioMap(typeMap);
        Map<String, Integer> diffMap = new LinkedHashMap<>();
        diffMap.put("1", 40);
        diffMap.put("2", 40);
        diffMap.put("3", 20);
        ratio.setDifficultyRatioMap(diffMap);
        return ratio;
    }
}
