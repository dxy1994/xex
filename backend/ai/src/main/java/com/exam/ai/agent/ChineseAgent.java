package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ChineseAgent extends BaseSubjectAgent {

    public ChineseAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        super(deepSeekClient, modelRouter, objectMapper);
    }

    @Override
    public String getSubjectName() { return "语文"; }

    @Override
    public String[] getQuestionTypes() {
        return new String[]{
                "SINGLE_CHOICE", "MULTI_CHOICE", "JUDGE",
                "FILL_BLANK", "DICTATION",
                "SHORT_ANSWER",
                "ESSAY", "MATERIAL_ANALYSIS", "WRITING_BIG", "WRITING_SMALL"
        };
    }

    @Override
    protected String getSubjectSpecificGenerateGuide() {
        return """
                语文出题原则：
                1. 字音、字形、成语、病句、修辞、标点、文学常识等基础知识可出选择题和判断题
                2. 古诗词默写（DICTATION）：给出上句补下句、给出下句补上句、根据情境默写、给出作者补篇名
                3. 文言文阅读：提供完整文言文段落，设实词释义、虚词用法、句子翻译、内容理解等小题
                4. 现代文阅读（MATERIAL_ANALYSIS）：提供完整短文，题目分理解和赏析两个层次
                5. 大作文（WRITING_BIG）：命题/话题/材料作文，500-800字，给出详细审题指引和评分标准
                6. 小作文（WRITING_SMALL）：应用文写作（书信、通知、倡议书、读后感等），100-300字
                7. 选择题干扰项要源于学生常见错误（形近字、音近字、成语误用等）
                8. 古诗文题要注明出处和作者""";
    }

    @Override
    protected String getSubjectSpecificCheckRules() {
        return """
                语文判分规则：
                - 字音字形/成语/病句：必须与标准答案完全一致，谐音别字0分
                - 古诗词默写：关键字（考点字）错误不得分；非关键字同音字酌情扣1-2分但允许
                - 文言文翻译：关键实词、虚词翻译正确即得分，句式不严格对应扣1-2分
                - 阅读理解简答题：按得分要点覆盖比例给分，表述方式不必与答案完全一致，答对60%以上要点即给及格分
                - 材料分析题：观点正确+材料结合+逻辑清晰=满分；观点偏颇但自圆其说给60%分
                - 大作文（满分通常60分）：内容30%+语言30%+结构20%+创意20%综合评分
                - 小作文（满分通常20分）：格式正确40%+内容完整40%+语言得体20%""";
    }

    @Override
    protected String getSubjectSpecificExplainGuide() {
        return """
                语文讲解风格：
                1. 字词题：解释正确读音/写法的由来，辨析易混点
                2. 成语题：讲清成语出处、本义、比喻义、使用语境
                3. 古诗词：逐句赏析意象、手法、情感，引用同类诗句对比
                4. 文言文：逐字逐句翻译，归纳虚词用法和特殊句式
                5. 阅读题：先梳理文章脉络，再逐题分析答题思路和赋分点
                6. 作文题：审题→立意→素材→结构，给出优秀范文片段""";
    }
}
