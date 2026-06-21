package com.exam.admin.controller;

import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/question")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) Integer difficulty) {
        return questionService.page(pageNum, pageSize, categoryId, type, difficulty);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return questionService.getById(id);
    }

    @PostMapping
    public Result<?> save(@RequestBody Question question) {
        return questionService.save(question);
    }

    @PutMapping
    public Result<?> update(@RequestBody Question question) {
        return questionService.update(question);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return questionService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        return questionService.batchDelete(ids);
    }
}
