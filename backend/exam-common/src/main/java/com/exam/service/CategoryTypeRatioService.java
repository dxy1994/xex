package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.entity.Category;
import com.exam.entity.CategoryTypeRatio;
import com.exam.mapper.CategoryMapper;
import com.exam.mapper.CategoryTypeRatioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryTypeRatioService {

    private final CategoryTypeRatioMapper ratioMapper;
    private final CategoryMapper categoryMapper;

    private static final Map<String, String[]> SUBJECT_TYPES = new LinkedHashMap<>();
    static {
        SUBJECT_TYPES.put("语文", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","DICTATION","SHORT_ANSWER","ESSAY","MATERIAL_ANALYSIS","WRITING_BIG","WRITING_SMALL"});
        SUBJECT_TYPES.put("数学", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","CALCULATION","PROOF","DERIVATION"});
        SUBJECT_TYPES.put("英语", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","CLOZE","SHORT_ANSWER","TRANSLATION","WRITING_BIG","WRITING_SMALL","CONTINUATION","LISTENING","ORAL"});
        SUBJECT_TYPES.put("物理", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","INDEFINITE_CHOICE","JUDGE","FILL_BLANK","CALCULATION","DERIVATION","EXPERIMENT","CHART_ANALYSIS"});
        SUBJECT_TYPES.put("化学", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","CALCULATION","EXPERIMENT","CHART_ANALYSIS"});
        SUBJECT_TYPES.put("政治", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","SHORT_ANSWER","TERM_EXPLAIN","ESSAY","MATERIAL_ANALYSIS"});
        SUBJECT_TYPES.put("历史", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","MAP_FILL","SHORT_ANSWER","TERM_EXPLAIN","ESSAY","MATERIAL_ANALYSIS"});
        SUBJECT_TYPES.put("地理", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","MAP_FILL","SHORT_ANSWER","CHART_ANALYSIS"});
        SUBJECT_TYPES.put("生物", new String[]{"SINGLE_CHOICE","MULTI_CHOICE","JUDGE","FILL_BLANK","SHORT_ANSWER","EXPERIMENT","CHART_ANALYSIS"});
    }

    public Result<?> getByCategoryId(Long categoryId) {
        LambdaQueryWrapper<CategoryTypeRatio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryTypeRatio::getCategoryId, categoryId);
        CategoryTypeRatio ratio = ratioMapper.selectOne(wrapper);
        if (ratio == null) {
            String subjectName = resolveSubjectName(categoryId);
            String[] types = getTypesForSubject(subjectName);
            ratio = CategoryTypeRatio.createDefault(categoryId, types);
        }
        return Result.success(ratio);
    }

    public Result<?> getRatioInfo(Long categoryId) {
        String subjectName = resolveSubjectName(categoryId);
        String[] types = getTypesForSubject(subjectName);
        LambdaQueryWrapper<CategoryTypeRatio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryTypeRatio::getCategoryId, categoryId);
        CategoryTypeRatio ratio = ratioMapper.selectOne(wrapper);
        if (ratio == null) {
            ratio = CategoryTypeRatio.createDefault(categoryId, types);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("subjectName", subjectName);
        result.put("availableTypes", types);
        result.put("ratio", ratio);
        return Result.success(result);
    }

    public Result<?> save(CategoryTypeRatio ratio) {
        validateRatios(ratio);
        LambdaQueryWrapper<CategoryTypeRatio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryTypeRatio::getCategoryId, ratio.getCategoryId());
        CategoryTypeRatio existing = ratioMapper.selectOne(wrapper);
        if (existing != null) {
            ratio.setId(existing.getId());
            ratioMapper.updateById(ratio);
        } else {
            ratio.setDeleted(0);
            ratioMapper.insert(ratio);
        }
        return Result.success();
    }

    private void validateRatios(CategoryTypeRatio ratio) {
        Map<String, Integer> typeMap = ratio.getTypeRatioMap();
        int typeSum = typeMap.values().stream().mapToInt(Integer::intValue).sum();
        if (typeSum != 100 && !typeMap.isEmpty()) {
            log.warn("题型比例总和为{}%，非100%", typeSum);
        }
        Map<String, Integer> diffMap = ratio.getDifficultyRatioMap();
        int diffSum = diffMap.values().stream().mapToInt(Integer::intValue).sum();
        if (diffSum != 100 && !diffMap.isEmpty()) {
            log.warn("难度比例总和为{}%，非100%", diffSum);
        }
    }

    private String resolveSubjectName(Long categoryId) {
        if (categoryId == null) return null;
        Category current = categoryMapper.selectById(categoryId);
        int maxDepth = 10;
        while (current != null && maxDepth > 0) {
            if (SUBJECT_TYPES.containsKey(current.getName())) {
                return current.getName();
            }
            if (current.getParentId() != null) {
                current = categoryMapper.selectById(current.getParentId());
            } else {
                break;
            }
            maxDepth--;
        }
        return null;
    }

    public String[] getTypesForSubject(String subjectName) {
        if (subjectName == null) return new String[]{"SINGLE_CHOICE","FILL_BLANK","JUDGE","CALCULATION"};
        String[] types = SUBJECT_TYPES.get(subjectName);
        return types != null ? types : new String[]{"SINGLE_CHOICE","FILL_BLANK","JUDGE","CALCULATION"};
    }
}
