package com.exam.admin.controller;

import com.exam.common.Result;
import com.exam.entity.Exam;
import com.exam.service.ExamService;
import com.exam.service.UserExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/exam")
@RequiredArgsConstructor
public class AdminExamController {

    private final ExamService examService;
    private final UserExamService userExamService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long categoryId) {
        return examService.listExams(categoryId);
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        return examService.getExamDetail(id);
    }

    @PostMapping("/fixed")
    public Result<?> createFixedExam(@RequestBody Map<String, Object> params) {
        Exam exam = new Exam();
        exam.setTitle((String) params.get("title"));
        exam.setCategoryId(Long.valueOf(params.get("categoryId").toString()));
        exam.setDuration(Integer.valueOf(params.get("duration").toString()));

        @SuppressWarnings("unchecked")
        List<Number> questionIdNums = (List<Number>) params.get("questionIds");
        List<Long> questionIds = questionIdNums.stream().map(Number::longValue).toList();

        return examService.createFixedExam(exam, questionIds);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return examService.deleteExam(id);
    }

    @GetMapping("/records")
    public Result<?> getAllRecords(@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return userExamService.getAllExamRecords(pageNum, pageSize);
    }
}
