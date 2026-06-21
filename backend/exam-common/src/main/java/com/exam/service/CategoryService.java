package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Category;
import com.exam.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    /** 63学制ID */
    public static final Long EDU_63_ID = 1L;
    /** 54学制ID */
    public static final Long EDU_54_ID = 2L;

    /** 63学制：一年级~六年级=小学，七年级~九年级=初中 */
    private static final Set<String> PRIMARY_63 = Set.of("一年级","二年级","三年级","四年级","五年级","六年级");
    /** 54学制：一年级~五年级=小学，六年级~九年级=初中 */
    private static final Set<String> PRIMARY_54 = Set.of("一年级","二年级","三年级","四年级","五年级");

    public Result<?> list() {
        return Result.success(tree());
    }

    public Result<?> page(int pageNum, int pageSize) {
        Page<Category> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        return Result.success(categoryMapper.selectPage(page, wrapper));
    }

    public Result<?> getById(Long id) {
        return Result.success(categoryMapper.selectById(id));
    }

    @Caching(evict = {
        @CacheEvict(value = "categoryTree", allEntries = true),
        @CacheEvict(value = "categoryTreeByEdu", allEntries = true),
        @CacheEvict(value = "categoryDescendants", allEntries = true)
    })
    public Result<?> save(Category category) {
        category.setDeleted(0);
        categoryMapper.insert(category);
        return Result.success();
    }

    @Caching(evict = {
        @CacheEvict(value = "categoryTree", allEntries = true),
        @CacheEvict(value = "categoryTreeByEdu", allEntries = true),
        @CacheEvict(value = "categoryDescendants", allEntries = true)
    })
    public Result<?> update(Category category) {
        categoryMapper.updateById(category);
        return Result.success();
    }

    @Caching(evict = {
        @CacheEvict(value = "categoryTree", allEntries = true),
        @CacheEvict(value = "categoryTreeByEdu", allEntries = true),
        @CacheEvict(value = "categoryDescendants", allEntries = true)
    })
    public Result<?> delete(Long id) {
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        Long childCount = categoryMapper.selectCount(wrapper);
        if (childCount > 0) {
            return Result.error("该分类下存在子分类，请先删除子分类");
        }
        categoryMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 返回所有分类的树结构（管理员用），缓存 1 小时
     */
    @Cacheable(value = "categoryTree")
    public List<Category> tree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> all = categoryMapper.selectList(wrapper);
        return buildTree(all);
    }

    /**
     * 返回指定学制下的树结构（用户端用），从年级开始，跳过学制层
     * 缓存 1 小时，按学制分别缓存
     * @param educationSystem "63" 或 "54"
     */
    @Cacheable(value = "categoryTreeByEdu", key = "#educationSystem")
    public List<Category> treeByEducationSystem(String educationSystem) {
        Long eduId = "54".equals(educationSystem) ? EDU_54_ID : EDU_63_ID;
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> all = categoryMapper.selectList(wrapper);

        // 找到该学制下的所有年级（直接子节点）
        List<Category> grades = all.stream()
            .filter(c -> eduId.equals(c.getParentId()))
            .collect(Collectors.toList());

        // 为每个年级标注学段标签
        for (Category grade : grades) {
            grade.setStageLabel(getStageLabel(grade.getName(), educationSystem));
        }

        // 构建以年级为根的完整树（递归包含所有子孙层级）
        return buildSubTree(grades, all);
    }

    /**
     * 根据年级名称和学制判断学段
     */
    private String getStageLabel(String gradeName, String educationSystem) {
        if ("54".equals(educationSystem)) {
            return PRIMARY_54.contains(gradeName) ? "小学" : "初中";
        }
        return PRIMARY_63.contains(gradeName) ? "小学" : "初中";
    }

    /**
     * 以指定节点为根构建子树
     */
    private List<Category> buildSubTree(List<Category> roots, List<Category> all) {
        Map<Long, List<Category>> childrenMap = all.stream()
            .filter(c -> c.getParentId() != null)
            .collect(Collectors.groupingBy(Category::getParentId));

        for (Category root : roots) {
            appendChildren(root, childrenMap);
        }
        return roots;
    }

    private void appendChildren(Category parent, Map<Long, List<Category>> childrenMap) {
        List<Category> children = childrenMap.getOrDefault(parent.getId(), Collections.emptyList());
        parent.setChildren(children);
        for (Category child : children) {
            appendChildren(child, childrenMap);
        }
    }

    /**
     * 获取某分类及其所有子孙分类的 ID 列表（包含自身），缓存 1 小时
     */
    @Cacheable(value = "categoryDescendants", key = "#categoryId")
    public List<Long> getAllDescendantIds(Long categoryId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> all = categoryMapper.selectList(wrapper);

        // 构建 parentId -> children 映射
        Map<Long, List<Category>> childrenMap = all.stream()
            .filter(c -> c.getParentId() != null)
            .collect(Collectors.groupingBy(Category::getParentId));

        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        collectDescendants(categoryId, childrenMap, result);
        return result;
    }

    private void collectDescendants(Long parentId, Map<Long, List<Category>> childrenMap, List<Long> result) {
        List<Category> children = childrenMap.get(parentId);
        if (children == null) return;
        for (Category child : children) {
            result.add(child.getId());
            collectDescendants(child.getId(), childrenMap, result);
        }
    }

    private List<Category> buildTree(List<Category> all) {
        Map<Long, Category> map = new LinkedHashMap<>();
        for (Category c : all) {
            c.setChildren(new ArrayList<>());
            map.put(c.getId(), c);
        }

        List<Category> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                Category parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(c);
                } else {
                    // 孤儿节点视为顶级
                    roots.add(c);
                }
            }
        }

        // 清除空的 children 列表，让 JSON 更简洁（保留空数组以便前端识别）
        return roots;
    }
}
