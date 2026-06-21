package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public Result<?> page(int pageNum, int pageSize, Long categoryId, String type, Integer difficulty) {
        Page<Question> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) wrapper.eq(Question::getCategoryId, categoryId);
        if (type != null && !type.isEmpty()) wrapper.eq(Question::getType, type);
        if (difficulty != null) wrapper.eq(Question::getDifficulty, difficulty);
        wrapper.orderByDesc(Question::getCreateTime);
        return Result.success(questionMapper.selectPage(page, wrapper));
    }

    public Result<?> getById(Long id) {
        return Result.success(questionMapper.selectById(id));
    }

    public List<Question> listByCategoryAndType(Long categoryId, String type) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getCategoryId, categoryId);
        if (type != null) wrapper.eq(Question::getType, type);
        return questionMapper.selectList(wrapper);
    }

    public Result<?> save(Question question) {
        question.setDeleted(0);
        questionMapper.insert(question);
        return Result.success();
    }

    public Result<?> update(Question question) {
        questionMapper.updateById(question);
        return Result.success();
    }

    public Result<?> delete(Long id) {
        questionMapper.deleteById(id);
        return Result.success();
    }

    public Result<?> batchDelete(List<Long> ids) {
        questionMapper.deleteBatchIds(ids);
        return Result.success();
    }
}
