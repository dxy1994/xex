package com.exam.ai.agent;

import com.exam.ai.config.DeepSeekClient;
import com.exam.ai.config.ModelRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class EnglishAgent extends BaseSubjectAgent {

    public EnglishAgent(DeepSeekClient deepSeekClient, ModelRouter modelRouter, ObjectMapper objectMapper) {
        super(deepSeekClient, modelRouter, objectMapper);
    }

    @Override
    public String getSubjectName() { return "英语"; }

    @Override
    public String[] getQuestionTypes() {
        return new String[]{
                "SINGLE_CHOICE", "MULTI_CHOICE", "JUDGE",
                "FILL_BLANK", "CLOZE",
                "SHORT_ANSWER", "TRANSLATION",
                "WRITING_BIG", "WRITING_SMALL", "CONTINUATION",
                "LISTENING", "ORAL"
        };
    }

    @Override
    protected String getSubjectSpecificGenerateGuide() {
        return """
                英语出题原则：
                1. 单项选择（SINGLE_CHOICE）：语法辨析、词汇用法、情景交际、短语搭配等
                2. 多选题（MULTI_CHOICE）：阅读多选题、语法正误多选等
                3. 填空（FILL_BLANK）：单词拼写、适当形式填空、首字母填空，给出句意或语境提示
                4. 完形填空（CLOZE）：提供一篇完整短文（150-300词），设10-20个空，每个空4个选项
                5. 简答（SHORT_ANSWER）：阅读理解简答题、根据短文回答问题
                6. 翻译（TRANSLATION）：中译英/英译中，答案标注关键字给分点
                7. 大作文（WRITING_BIG）：命题作文/图表作文/议论文，120-200词
                8. 小作文（WRITING_SMALL）：应用文（书信/通知/邮件/日记等），60-100词
                9. 读后续写（CONTINUATION）：提供故事开头，续写两段，每段开头语已给出
                10. 听力（LISTENING）：题干需含听力原文或明确描述听力场景和题目
                11. 口语（ORAL）：朗读/复述/即兴表达题，答案含评分标准
                12. 词汇和语法难度严格对应学段水平，超纲词汇可标注中文""";
    }

    @Override
    protected String getSubjectSpecificCheckRules() {
        return """
                英语判分规则：
                - 单选/判断：与标准答案完全一致得满分
                - 单词拼写：完全正确=满分；大小写错误扣一半分；拼写错误=0分
                - 适当形式填空：单词选对但形式错误=0分（如该用过去式却用原形）
                - 完形填空：与标准答案一致得分
                - 阅读简答：答出关键词即给分，语法小错不扣分
                - 翻译题：关键词语翻译正确即给分，表达方式可不同；每个关键点赋分
                - 大作文：内容完整性30%+语法准确度25%+词汇丰富度20%+结构逻辑15%+书写规范10%
                - 小作文：格式40%+内容完整40%+语言得体20%
                - 读后续写：情节合理40%+语言表达30%+衔接连贯30%
                - 听力/口语：按评分标准项目给分""";
    }

    @Override
    protected String getSubjectSpecificExplainGuide() {
        return """
                英语讲解风格：
                1. 语法题：分析题干语法结构，解释正确选项的语法依据，辨析错误选项错在哪里
                2. 词汇题：讲解词义区别、搭配习惯、语境用法，提供例句
                3. 完形填空：先通读全文把握大意，再逐空分析上下文线索
                4. 阅读理解：先概括篇章结构，再逐题分析定位句和选答依据
                5. 翻译题：对比中英文表达差异，讲解翻译技巧（增译/省译/词性转换）
                6. 作文题：审题→列提纲→写作模板→范文参考→常见语法错误提醒""";
    }
}
