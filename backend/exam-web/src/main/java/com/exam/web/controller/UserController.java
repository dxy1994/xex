package com.exam.web.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.exam.common.Result;
import com.exam.entity.WebUser;
import com.exam.mapper.WebUserMapper;
import com.exam.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final WebUserMapper webUserMapper;
    private final UserPreferenceService userPreferenceService;

    @GetMapping("/info")
    public Result<?> getUserInfo(@AuthenticationPrincipal WebUser user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("email", user.getEmail());
        info.put("userType", user.getUserType());
        info.put("educationSystem", user.getEducationSystem() != null ? user.getEducationSystem() : "63");
        return Result.success(info);
    }

    /**
     * 保存用户学制偏好
     */
    @PutMapping("/education-system")
    public Result<?> updateEducationSystem(@AuthenticationPrincipal WebUser user,
                                           @RequestBody Map<String, String> body) {
        String eduSystem = body.get("educationSystem");
        if (eduSystem == null || (!eduSystem.equals("63") && !eduSystem.equals("54"))) {
            return Result.error("无效的学制，仅支持 63 或 54");
        }
        LambdaUpdateWrapper<WebUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WebUser::getId, user.getId());
        wrapper.set(WebUser::getEducationSystem, eduSystem);
        webUserMapper.update(null, wrapper);
        return Result.success();
    }

    /**
     * 保存用户最后浏览的分类层级路径（Redis，8天有效，最多5级）
     */
    @SuppressWarnings("unchecked")
    @PutMapping("/grade-selection")
    public Result<?> saveGradeSelection(@AuthenticationPrincipal WebUser user,
                                        @RequestBody Map<String, Object> body) {
        Object pathObj = body.get("path");
        if (pathObj == null) {
            return Result.error("path 不能为空");
        }
        List<Long> path;
        if (pathObj instanceof List) {
            path = ((List<?>) pathObj).stream()
                    .map(o -> o instanceof Number ? ((Number) o).longValue() : Long.valueOf(o.toString()))
                    .toList();
        } else {
            return Result.error("path 格式错误，应为ID数组");
        }
        userPreferenceService.saveGradeSelection(user.getId(), path);
        return Result.success();
    }

    /**
     * 获取用户最后浏览的分类层级路径
     */
    @GetMapping("/grade-selection")
    public Result<?> getGradeSelection(@AuthenticationPrincipal WebUser user) {
        List<Long> path = userPreferenceService.getGradeSelection(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        return Result.success(result);
    }

    /**
     * 清除用户年级选择缓存（登出时调用）
     */
    @DeleteMapping("/grade-selection")
    public Result<?> clearGradeSelection(@AuthenticationPrincipal WebUser user) {
        userPreferenceService.clearGradeSelection(user.getId());
        return Result.success();
    }
}
