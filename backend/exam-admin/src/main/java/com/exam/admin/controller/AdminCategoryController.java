package com.exam.admin.controller;

import com.exam.common.Result;
import com.exam.entity.Category;
import com.exam.entity.CategoryTypeRatio;
import com.exam.service.CategoryService;
import com.exam.service.CategoryTypeRatioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryTypeRatioService ratioService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize) {
        return categoryService.page(pageNum, pageSize);
    }

    @GetMapping("/tree")
    public Result<?> tree() {
        return Result.success(categoryService.tree());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public Result<?> save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping
    public Result<?> update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }

    @GetMapping("/ratio/{categoryId}")
    public Result<?> getRatio(@PathVariable Long categoryId) {
        return ratioService.getByCategoryId(categoryId);
    }

    @GetMapping("/ratio/info/{categoryId}")
    public Result<?> getRatioInfo(@PathVariable Long categoryId) {
        return ratioService.getRatioInfo(categoryId);
    }

    @PostMapping("/ratio")
    public Result<?> saveRatio(@RequestBody CategoryTypeRatio ratio) {
        return ratioService.save(ratio);
    }
}
