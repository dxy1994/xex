package com.exam.web.controller;

import com.exam.common.Result;
import com.exam.dto.ExamConfigDTO;
import com.exam.dto.SubmitExamDTO;
import com.exam.entity.WebUser;
import com.exam.service.CategoryService;
import com.exam.service.CategoryTypeRatioService;
import com.exam.service.ExamService;
import com.exam.service.UserExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final CategoryService categoryService;
    private final CategoryTypeRatioService ratioService;
    private final ExamService examService;
    private final UserExamService userExamService;

    @GetMapping("/categories")
    public Result<?> getCategories(@RequestParam(defaultValue = "63") String eduSystem) {
        return Result.success(categoryService.treeByEducationSystem(eduSystem));
    }

    @GetMapping("/category/{id}/ratio")
    public Result<?> getRatio(@PathVariable Long id) {
        return ratioService.getByCategoryId(id);
    }

    @PostMapping("/generate")
    public Result<?> generateExam(@Valid @RequestBody ExamConfigDTO configDTO) {
        return examService.generateRandomExam(configDTO);
    }

    /**
     * 基于种子号重新生成试卷（相同种子+相同配置→相同试卷）
     */
    @PostMapping("/generate-by-seed")
    public Result<?> generateBySeed(@RequestBody Map<String, Object> params) {
        String seed = (String) params.get("seed");
        Long categoryId = params.get("categoryId") != null
                ? Long.valueOf(params.get("categoryId").toString()) : null;
        return examService.generateBySeed(seed, categoryId);
    }

    /**
     * 根据种子号查询试卷信息（预览种子对应的试卷配置）
     */
    @GetMapping("/seed/{seed}")
    public Result<?> getExamBySeed(@PathVariable String seed) {
        return examService.getExamBySeed(seed);
    }

    @GetMapping("/{examId}")
    public Result<?> getExamDetail(@PathVariable Long examId) {
        return examService.getExamDetail(examId);
    }

    @PostMapping("/{examId}/start")
    public Result<?> startExam(@AuthenticationPrincipal WebUser user, @PathVariable Long examId) {
        Long userId = user != null ? user.getId() : 0L;
        return userExamService.startExam(userId, examId);
    }

    @PostMapping("/submit")
    public Result<?> submitExam(@Valid @RequestBody SubmitExamDTO submitDTO) {
        return userExamService.submitExam(submitDTO);
    }

    @GetMapping("/history")
    public Result<?> getHistory(@AuthenticationPrincipal WebUser user) {
        return userExamService.getUserExamHistory(user.getId());
    }

    @GetMapping("/record/{userExamId}")
    public Result<?> getRecordDetail(@PathVariable Long userExamId) {
        return userExamService.getUserExamDetail(userExamId);
    }
}
