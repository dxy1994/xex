package com.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户偏好服务 — 将年级选择及分类层级路径存入 Redis
 */
@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final StringRedisTemplate stringRedisTemplate;

    /** Redis key 前缀 */
    private static final String GRADE_KEY_PREFIX = "user:grade:";

    /** 8 天有效期（秒） */
    private static final long GRADE_TTL_SECONDS = 8 * 24 * 3600;

    /** 最大保存层级深度 */
    private static final int MAX_PATH_DEPTH = 5;

    /**
     * 保存用户最后浏览的分类层级路径（最多5级）
     * @param userId 用户ID
     * @param path   从年级到当前节点的完整ID路径，如 [gradeId, courseId, volumeId, chapterId, subChapterId]
     */
    public void saveGradeSelection(Long userId, List<Long> path) {
        if (path == null || path.isEmpty()) return;
        // 限制最大层级
        List<Long> limited = path.size() > MAX_PATH_DEPTH
                ? path.subList(0, MAX_PATH_DEPTH)
                : path;
        String value = limited.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String key = GRADE_KEY_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(key, value, GRADE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 获取用户最后浏览的分类层级路径，不存在返回空列表
     */
    public List<Long> getGradeSelection(Long userId) {
        String key = GRADE_KEY_PREFIX + userId;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null || value.isEmpty()) return Collections.emptyList();
        try {
            return Arrays.stream(value.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 清除用户年级选择缓存（登出时调用）
     */
    public void clearGradeSelection(Long userId) {
        stringRedisTemplate.delete(GRADE_KEY_PREFIX + userId);
    }
}
