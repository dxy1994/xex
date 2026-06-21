package com.exam.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * DeepSeek 多模型配置属性
 * <p>
 * 绑定 application.yml 中 spring.ai.deepseek 前缀的配置，
 * 支持多个模型（chat / reasoner / 未来 vision 等）各自独立配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.ai.deepseek")
public class DeepSeekProperties {

    /** DeepSeek API Key */
    private String apiKey;

    /** 模型配置映射：key=模型标识（如 chat、reasoner），value=模型参数 */
    private Map<String, ModelConfig> models = new HashMap<>();

    /**
     * 单个模型的配置参数
     */
    @Data
    public static class ModelConfig {
        /** 模型名称（如 deepseek-chat、deepseek-reasoner） */
        private String model;
        /** 采样温度 0~2，默认 0.7 */
        private Double temperature = 0.7;
        /** 最大输出 token 数 */
        private Integer maxTokens = 4096;
    }

}
