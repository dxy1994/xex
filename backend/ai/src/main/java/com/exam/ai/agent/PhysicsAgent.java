package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PhysicsAgent extends BaseSubjectAgent {

    public PhysicsAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        super(deepSeekClient, modelRouter, objectMapper);
    }

    @Override
    public String getSubjectName() { return "物理"; }

    @Override
    public String[] getQuestionTypes() {
        return new String[]{
                "SINGLE_CHOICE", "MULTI_CHOICE", "INDEFINITE_CHOICE", "JUDGE",
                "FILL_BLANK",
                "CALCULATION", "DERIVATION",
                "EXPERIMENT",
                "CHART_ANALYSIS"
        };
    }

    @Override
    protected String getSubjectSpecificGenerateGuide() {
        return """
                物理出题原则：
                1. 单选题（SINGLE_CHOICE）：概念辨析、现象判断、公式选择、数值估算等
                2. 多选题（MULTI_CHOICE）：正确说法有哪些、物理量关系判断等，明确标注"多选"
                3. 不定项选（INDEFINITE_CHOICE）：正确答案个数不固定，是高考拉分题型
                4. 判断题（JUDGE）：物理规律正误、现象解释真伪、实验操作规范判断
                5. 填空题（FILL_BLANK）：单位换算、物理常量、公式缺项、计算结果填空等
                6. 计算题（CALCULATION）：力学/电学/热学/光学计算，给出完整已知和图形描述
                7. 推导题（DERIVATION）：从基本公式推出新关系式，如推导某一物理量的表达式
                8. 实验题（EXPERIMENT）：实验原理、器材选择、步骤设计、数据处理、误差分析
                9. 图表分析（CHART_ANALYSIS）：根据v-t图/s-t图/U-I图等分析物理过程
                10. 物理量和单位必须规范，统一使用国际单位制
                11. 重力加速度g值需注明（10N/kg或9.8N/kg）
                12. 电路题需描述电路结构和元件参数""";
    }

    @Override
    protected String getSubjectSpecificCheckRules() {
        return """
                物理判分规则：
                - 单选/判断：答案完全一致得满分，否则0分
                - 多选：全部选对=满分；漏选=一半分；错选=0分
                - 不定项选：按正确选项比例给分
                - 填空题：数值正确+单位正确=满分；数值正确但单位错误/缺失=扣一半分
                - 计算题：公式正确+代入正确+结果正确+单位正确=满分；公式正确计算错误=60%分；缺单位扣1-2分
                - 推导题：推导链完整+结论正确=满分；思路正确但细节有误=60%-80%分
                - 实验题：原理正确+步骤完整+数据处理正确=满分；原理正确步骤不完整=60%-80%分
                - 图表分析：关键点识别正确+分析合理=满分""";
    }

    @Override
    protected String getSubjectSpecificExplainGuide() {
        return """
                物理讲解风格：
                1. 先分析题目涉及的物理概念和定律
                2. 列出相关公式及其适用条件
                3. 逐步写出推导或计算过程
                4. 强调物理量单位换算和有效数字
                5. 画图辅助理解（文字描述受力分析图、电路图等）
                6. 联系生活实际加深理解，总结同类题型解题模板""";
    }
}
