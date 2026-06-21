package com.exam.admin.controller;

import com.exam.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    @Value("${app.upload.path:./uploads}")
    private String uploadBasePath;

    /** 分类封面图片上传 */
    @PostMapping("/category")
    public Result<?> uploadCategoryImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 仅允许图片类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("仅支持上传图片文件");
        }

        // 限制大小 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过 5MB");
        }

        try {
            // 按日期分子目录：uploads/categories/2026-06-19/xxx.jpg
            String dateDir = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            Path uploadDir = Paths.get(uploadBasePath, "categories", dateDir);
            Files.createDirectories(uploadDir);

            Path targetPath = uploadDir.resolve(newFileName);
            file.transferTo(targetPath.toFile());

            // 返回可访问的相对路径
            String relativePath = "/uploads/categories/" + dateDir + "/" + newFileName;
            return Result.success(relativePath);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
