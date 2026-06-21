package com.exam.ai.agent;

import com.exam.entity.Category;
import com.exam.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目Agent注册中心 - 根据分类名称路由到对应的科目Agent
 */
@Slf4j
@Component
public class SubjectAgentRegistry {

    private final Map<String, SubjectAgent> agentMap = new LinkedHashMap<>();
    private final CategoryMapper categoryMapper;

    /** Spring自动注入所有 SubjectAgent 实现 */
    @Autowired
    public SubjectAgentRegistry(List<SubjectAgent> agents, CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
        for (SubjectAgent agent : agents) {
            agentMap.put(agent.getSubjectName(), agent);
            log.info("【Agent注册】已注册科目Agent: {} (题型: {})",
                    agent.getSubjectName(), String.join(",", agent.getQuestionTypes()));
        }
    }

    /**
     * 根据分类ID查找对应的科目Agent
     * 从叶子分类向上追溯，找到课程级别的分类名称来匹配Agent
     */
    public SubjectAgent resolveByCategoryId(Long categoryId) {
        String subjectName = resolveSubjectName(categoryId);
        SubjectAgent agent = agentMap.get(subjectName);
        if (agent != null) {
            log.debug("【Agent路由】categoryId={} -> 科目={} -> Agent={}",
                    categoryId, subjectName, agent.getClass().getSimpleName());
            return agent;
        }
        log.warn("【Agent路由】categoryId={} 未找到对应科目Agent, subjectName={}", categoryId, subjectName);
        return null;
    }

    /**
     * 根据科目名称查找Agent
     */
    public SubjectAgent resolveBySubjectName(String subjectName) {
        return agentMap.get(subjectName);
    }

    /**
     * 获取所有已注册的科目名称
     */
    public String[] getRegisteredSubjects() {
        return agentMap.keySet().toArray(new String[0]);
    }

    /**
     * 从分类ID追溯科目名称
     * 分类层级：学制 -> 年级 -> 课程(科目) -> 分册 -> 章节 -> 知识点
     * 科目在第三层（课程级别），名称如"语文""数学""英语"
     */
    public String resolveSubjectName(Long categoryId) {
        if (categoryId == null) return null;

        // 从当前分类向上追溯，找到课程层级
        Category current = categoryMapper.selectById(categoryId);
        int maxDepth = 10; // 防止无限循环
        while (current != null && maxDepth > 0) {
            // 如果当前分类名是已知科目名，直接返回
            if (agentMap.containsKey(current.getName())) {
                return current.getName();
            }
            // 如果当前分类有父级，继续向上
            if (current.getParentId() != null) {
                current = categoryMapper.selectById(current.getParentId());
            } else {
                break;
            }
            maxDepth--;
        }
        return null;
    }
}
