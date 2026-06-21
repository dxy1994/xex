package com.exam.ai.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 模型路由器 —— 根据任务类型决定使用哪个模型。
 * <p>
 * 路由规则：
 * <ul>
 *   <li>CHECK_ANSWER（答案检查）→ chat 模型（快速、低成本）</li>
 *   <li>GENERATE_QUESTION（试题生成）→ reasoner 模型（深度推理）</li>
 *   <li>EXPLAIN_QUESTION（试题讲解）→ reasoner 模型（深度推理）</li>
 * </ul>
 * <p>
 * 未来扩展：新增 TaskType 枚举值 + 对应路由规则即可。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelRouter {

    private final DeepSeekProperties properties;

    /**
     * 根据任务类型选择模型 key
     *
     * @param taskType 任务类型
     * @return 模型 key（对应 application.yml 中 models 下的 key）
     * @throws IllegalStateException 如果配置中未找到对应模型
     */
    public String selectModel(TaskType taskType) {
        String modelKey = route(taskType);
        if (!properties.getModels().containsKey(modelKey)) {
            throw new IllegalStateException(
                    "未找到模型配置: " + modelKey + "，请在 application.yml 中配置 spring.ai.deepseek.models." + modelKey);
        }
        log.debug("【ModelRouter】任务类型={}, 选择模型={}", taskType, modelKey);
        return modelKey;
    }

    /**
     * 根据任务类型查找对应的 ModelConfig
     */
    public DeepSeekProperties.ModelConfig getModelConfig(TaskType taskType) {
        String modelKey = selectModel(taskType);
        return properties.getModels().get(modelKey);
    }

    private String route(TaskType taskType) {
        return switch (taskType) {
            case CHECK_ANSWER -> "chat";
            case GENERATE_QUESTION, EXPLAIN_QUESTION -> "reasoner";
        };
    }

}
