package com.exam.ai.dto;

import java.util.List;

/**
 * 试题题型枚举 —— 7大类，20+细分题型
 * <p>
 * 设计参考中高考命题分类体系，覆盖全部学科的差异化需求。
 */
public enum QuestionType {

    // ═══════════════════ 一、选择判断类 ═══════════════════
    /** 单选题（四选一，唯一正确答案） */
    SINGLE_CHOICE("单选", Category.CHOICE_JUDGE,
            "4个选项中有且仅有一个正确答案，格式：options为JSON数组如[\"A.xxx\",\"B.xxx\",\"C.xxx\",\"D.xxx\"]，answer为正确选项字母"),
    /** 多选题（四选多，全部选对才得分） */
    MULTI_CHOICE("多选", Category.CHOICE_JUDGE,
            "4个选项中有多个正确答案，格式：options同上，answer为选项字母组合如\"ABD\""),
    /** 不定项选择（选项数量不限，答对得分，部分对酌情给分） */
    INDEFINITE_CHOICE("不定项选", Category.CHOICE_JUDGE,
            "选项数量不固定（4~6个），正确答案可能1个或多个，answer为选项字母组合"),
    /** 判断对错 */
    JUDGE("判断", Category.CHOICE_JUDGE,
            "判断陈述是否正确，answer为「正确」或「错误」（或true/false）"),

    // ═══════════════════ 二、填空补充类 ═══════════════════
    /** 纯填空题（空白处填入字词、数值、公式等） */
    FILL_BLANK("填空", Category.FILL_SUPPLEMENT,
            "题目中留空（用___表示），answer为应填内容，多个空用分号分隔"),
    /** 完形填空（英语短文+选项） */
    CLOZE("完形填空", Category.FILL_SUPPLEMENT,
            "提供短文，设若干空，每个空配4个选项；options为二维数组[[\"A.xx\",\"B.xx\",...],...]，answer为选项字母序列如\"ABCD\"或以逗号分隔"),
    /** 填图题（地理填图、生物结构填图等） */
    MAP_FILL("填图", Category.FILL_SUPPLEMENT,
            "题干描述或附图名称，answer为各标注点的名称，如\"①太平洋 ②大西洋\""),
    /** 默写题（古诗词、名句、公式等） */
    DICTATION("默写", Category.FILL_SUPPLEMENT,
            "给出上句/下句/标题/作者，answer为完整的默写内容"),

    // ═══════════════════ 三、简短表达类 ═══════════════════
    /** 简答题 */
    SHORT_ANSWER("简答", Category.SHORT_EXPRESSION,
            "用简短文字回答问题，answer为要点式标准答案，判分时语义匹配"),
    /** 名词解释 */
    TERM_EXPLAIN("名词解释", Category.SHORT_EXPRESSION,
            "解释专业术语，answer为定义性文字，判分时看是否覆盖关键要素"),
    /** 翻译题（中译英/英译中） */
    TRANSLATION("翻译", Category.SHORT_EXPRESSION,
            "将给定内容翻译为目标语言，answer为标准译文，判分时按关键词给分"),

    // ═══════════════════ 四、长篇输出类 ═══════════════════
    /** 论述题/问答题 */
    ESSAY("论述", Category.LONG_OUTPUT,
            "对某一问题进行深入分析和论述，answer为要点得分点，判分按点给分"),
    /** 材料分析题 */
    MATERIAL_ANALYSIS("材料分析", Category.LONG_OUTPUT,
            "提供背景材料，要求分析并回答问题，answer包含核心论点和依据"),
    /** 大作文（命题/话题/材料作文） */
    WRITING_BIG("大作文", Category.LONG_OUTPUT,
            "根据题目要求写一篇完整文章（通常500-800字），answer为写作要点和评分标准"),
    /** 小作文/应用文（书信、通知、倡议书等） */
    WRITING_SMALL("小作文", Category.LONG_OUTPUT,
            "根据情境写应用文（通常100-300字），answer包含格式要点和内容要素"),
    /** 读后续写（英语题型） */
    CONTINUATION("读后续写", Category.LONG_OUTPUT,
            "阅读所给材料，根据其内容和所给段落开头语续写两段，answer为续写要点和衔接方法"),

    // ═══════════════════ 五、计算推理类 ═══════════════════
    /** 计算题 */
    CALCULATION("计算", Category.CALCULATION_REASONING,
            "给出已知条件，要求进行数学/物理/化学计算，answer需包含完整解题过程和最终结果"),
    /** 证明题 */
    PROOF("证明", Category.CALCULATION_REASONING,
            "证明某个命题或结论，answer为逻辑严密的证明步骤"),
    /** 推导题 */
    DERIVATION("推导", Category.CALCULATION_REASONING,
            "从已知公式/定理推导出新的结论，answer为逐步推导过程"),

    // ═══════════════════ 六、实践操作类 ═══════════════════
    /** 实验题（实验设计/实验填空/误差分析） */
    EXPERIMENT("实验", Category.PRACTICAL_OPERATION,
            "涉及实验原理、步骤设计、数据处理、误差分析等，answer需包含实验要点和结论"),
    /** 绘画/制图题 */
    DRAWING("绘图", Category.PRACTICAL_OPERATION,
            "要求绘制示意图/坐标图/电路图等，answer描述绘图要求和关键标注"),
    /** 听力题 */
    LISTENING("听力", Category.PRACTICAL_OPERATION,
            "听录音材料后回答问题，题干含听力原文或描述，answer为正确答案"),
    /** 口语表达 */
    ORAL("口语", Category.PRACTICAL_OPERATION,
            "朗读、复述、即兴表达等，answer为评分标准"),

    // ═══════════════════ 七、综合探究类 ═══════════════════
    /** 开放型试题 */
    OPEN_ENDED("开放", Category.COMPREHENSIVE_INQUIRY,
            "无唯一标准答案，鼓励多角度思考，answer为评分维度（观点、论证、创新性等）"),
    /** 研究性学习课题 */
    RESEARCH("研究课题", Category.COMPREHENSIVE_INQUIRY,
            "探究某个主题，要求提出假设、设计研究方案、分析数据等"),
    /** 图表/数据解读题 */
    CHART_ANALYSIS("图表分析", Category.COMPREHENSIVE_INQUIRY,
            "根据图表、数据进行分析和判断，answer包含解读要点和结论");

    // ========== 枚举定义 ==========

    private final String label;
    private final Category category;
    private final String formatGuide;

    QuestionType(String label, Category category, String formatGuide) {
        this.label = label;
        this.category = category;
        this.formatGuide = formatGuide;
    }

    public String getLabel() { return label; }
    public Category getCategory() { return category; }
    public String getFormatGuide() { return formatGuide; }

    /**
     * 题型大类
     */
    public enum Category {
        CHOICE_JUDGE("选择判断类"),
        FILL_SUPPLEMENT("填空补充类"),
        SHORT_EXPRESSION("简短表达类"),
        LONG_OUTPUT("长篇输出类"),
        CALCULATION_REASONING("计算推理类"),
        PRACTICAL_OPERATION("实践操作类"),
        COMPREHENSIVE_INQUIRY("综合探究类");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    /**
     * 根据type字符串查找枚举
     */
    public static QuestionType fromCode(String code) {
        if (code == null) return SINGLE_CHOICE;
        try {
            return QuestionType.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 兼容旧代码
            return switch (code.toUpperCase()) {
                case "CHOICE" -> SINGLE_CHOICE;
                case "FILL" -> FILL_BLANK;
                case "APPLICATION" -> CALCULATION;
                default -> SINGLE_CHOICE;
            };
        }
    }

    /**
     * 获取某大类的所有题型
     */
    public static List<QuestionType> byCategory(Category category) {
        return java.util.Arrays.stream(values())
                .filter(t -> t.category == category)
                .toList();
    }
}
