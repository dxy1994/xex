package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class MathAgent extends BaseSubjectAgent {

    public MathAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        super(deepSeekClient, modelRouter, objectMapper);
    }

    @Override
    public String getSubjectName() { return "数学"; }

    @Override
    public String[] getQuestionTypes() {
        return new String[]{
                "SINGLE_CHOICE", "MULTI_CHOICE", "JUDGE",
                "FILL_BLANK",
                "CALCULATION", "PROOF", "DERIVATION"
        };
    }

    @Override
    protected String getSubjectSpecificGenerateGuide() {
        return """
                数学出题原则：
                1. 选择题（SINGLE_CHOICE/MULTI_CHOICE）：概念辨析、最优解法选择、数值估算等；多选题正确答案≥2个，标注"多选"
                2. 判断题（JUDGE）：数学命题正误、定理条件判断、公式适用范围判断
                3. 填空题（FILL_BLANK）：计算填空、公式缺项补充、定理条件填空等
                4. 计算题（CALCULATION）：给出完整已知条件，要求展示解题步骤；数值设计合理，答案为整数或常见分数
                5. 证明题（PROOF）：几何证明、代数恒等式证明、不等式证明等；答案需完整证明链
                6. 推导题（DERIVATION）：从已知公式/定理推出新结论，答案含逐步推导过程
                7. 几何题需明确描述图形和标注，复杂图形可文字描述
                8. 每道题必须附带详细解析（解题思路+具体步骤）""";
    }

    @Override
    protected String getSubjectSpecificCheckRules() {
        return """
                数学判分规则：
                - 单选/判断：答案完全一致得满分，否则0分
                - 多选题：全部选对得满分，漏选得一半分，错选得0分
                - 填空题：数值或表达式等价即正确（如1/2=0.5，x²+2x+1=(x+1)²）
                - 计算题：最终答案正确+过程完整=满分；思路正确但计算错误=60%-70%分；答案正确但过程缺失=70%-80%分
                - 证明题：逻辑完整+步骤严谨=满分；关键步骤缺失酌情扣分；证法不同但正确=满分
                - 推导题：过程正确+结论正确=满分；过程合理但细节有误=60%-80%分""";
    }

    @Override
    protected String getSubjectSpecificExplainGuide() {
        return """
                数学讲解风格：
                1. 先分析题目考查的知识点和数学思想（数形结合/分类讨论/化归转化等）
                2. 给出清晰的解题思路和切入点
                3. 逐步写出推导或计算过程（含公式、定理引用）
                4. 一题多解时给出最优解法并对比
                5. 总结解题模板、易错点和同类题规律
                6. 几何题可搭配辅助线思路说明""";
    }
}
