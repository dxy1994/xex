package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ChemistryAgent extends BaseSubjectAgent {

    public ChemistryAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        super(deepSeekClient, modelRouter, objectMapper);
    }

    @Override
    public String getSubjectName() { return "化学"; }

    @Override
    public String[] getQuestionTypes() {
        return new String[]{
                "SINGLE_CHOICE", "MULTI_CHOICE", "JUDGE",
                "FILL_BLANK",
                "CALCULATION",
                "EXPERIMENT",
                "CHART_ANALYSIS"
        };
    }

    @Override
    protected String getSubjectSpecificGenerateGuide() {
        return """
                化学出题原则：
                1. 单选题（SINGLE_CHOICE）：物质性质辨析、反应类型判断、概念理解、化学与生活等
                2. 多选题（MULTI_CHOICE）：正确操作选哪些、离子共存判断等，明确标注"多选"
                3. 判断题（JUDGE）：化学原理正误、实验操作规范、反应现象描述判断
                4. 填空题（FILL_BLANK）：化学式书写、化学方程式缺项补充、物质名称、仪器名称等
                5. 计算题（CALCULATION）：相对分子质量计算、化学方程式计算、溶液浓度计算、产率计算等
                6. 实验题（EXPERIMENT）：实验方案设计、物质推断与检验、气体制备与收集、除杂与分离
                7. 图表分析（CHART_ANALYSIS）：溶解度曲线分析、反应速率曲线、pH变化曲线等
                8. 化学式和方程式必须规范书写（数字下标，如H2O、CO2）
                9. 化学反应需注明条件和状态符号（↑↓）
                10. 计算题涉及相对原子质量时需在题目中给出数据""";
    }

    @Override
    protected String getSubjectSpecificCheckRules() {
        return """
                化学判分规则：
                - 单选/判断：答案完全一致得满分，否则0分
                - 多选：全部选对=满分；漏选=一半分；错选=0分
                - 填空题：化学式/物质名称正确=满分；化学式书写不规范（如数字没下标）酌情扣1分
                - 化学方程式：反应物生成物正确+配平正确+条件齐全=满分；未配平=扣一半分；缺条件或状态符号=扣1-2分
                - 计算题：公式正确+数据代入正确+结果正确+单位正确=满分；思路正确计算有误=60%分
                - 实验题：推断过程正确+结论正确=满分；推断思路正确个别物质错误=60%分
                - 图表分析：正确读取数据+分析合理=满分""";
    }

    @Override
    protected String getSubjectSpecificExplainGuide() {
        return """
                化学讲解风格：
                1. 先分析题目涉及的化学概念和原理
                2. 书写完整的化学方程式并配平，标注反应条件和现象
                3. 实验题解释每一步操作的目的和现象原因
                4. 推断题给出完整的推理链（特征反应→物质→验证）
                5. 计算题逐步展示公式代入和单位换算
                6. 强调化学用语的规范性，总结同类题型解题技巧""";
    }
}
