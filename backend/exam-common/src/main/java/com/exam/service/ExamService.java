package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.dto.ExamConfigDTO;
import com.exam.entity.*;
import com.exam.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamMapper examMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final CategoryTypeRatioMapper ratioMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    private static final String SEED_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int SEED_LENGTH = 6;
    private static final SecureRandom SEED_GENERATOR = new SecureRandom();

    /**
     * 生成随机种子号（6位，排除易混淆字符 0/O/1/I）
     */
    private String generateSeed() {
        StringBuilder sb = new StringBuilder(SEED_LENGTH);
        for (int i = 0; i < SEED_LENGTH; i++) {
            sb.append(SEED_CHARS.charAt(SEED_GENERATOR.nextInt(SEED_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 随机组卷：根据分类、题型比例、难度比例随机生成试卷
     * 支持种子号：相同种子+相同配置→相同试卷
     */
    @Transactional
    public Result<?> generateRandomExam(ExamConfigDTO configDTO) {
        Long categoryId = configDTO.getCategoryId();
        int totalQuestions = configDTO.getTotalQuestions() != null ? configDTO.getTotalQuestions() : 20;
        int duration = configDTO.getDuration() != null ? configDTO.getDuration() : 60;
        String seed = (configDTO.getSeed() != null && !configDTO.getSeed().isBlank())
                ? configDTO.getSeed().trim().toUpperCase()
                : generateSeed();

        // 获取题型比例配置
        LambdaQueryWrapper<CategoryTypeRatio> ratioWrapper = new LambdaQueryWrapper<>();
        ratioWrapper.eq(CategoryTypeRatio::getCategoryId, categoryId);
        CategoryTypeRatio ratio = ratioMapper.selectOne(ratioWrapper);
        if (ratio == null) {
            ratio = new CategoryTypeRatio();
            ratio.setTypeRatioMap(Map.of("SINGLE_CHOICE", 60, "FILL_BLANK", 20, "JUDGE", 10, "CALCULATION", 10));
            ratio.setDifficultyRatioMap(Map.of("1", 40, "2", 40, "3", 20));
        }

        // 解析题型比例 → 各题型数量
        Map<String, Integer> typeRatios = ratio.getTypeRatioMap();
        Map<String, Integer> typeCounts = new LinkedHashMap<>();
        int allocated = 0;
        List<String> typeKeys = new ArrayList<>(typeRatios.keySet());
        for (int i = 0; i < typeKeys.size(); i++) {
            String typeKey = typeKeys.get(i);
            if (i == typeKeys.size() - 1) {
                typeCounts.put(typeKey, totalQuestions - allocated);
            } else {
                int count = (int) Math.round(totalQuestions * typeRatios.get(typeKey) / 100.0);
                typeCounts.put(typeKey, Math.max(count, 0));
                allocated += count;
            }
        }

        // 解析难度比例 → 各难度题目数
        Map<String, Integer> diffRatios = ratio.getDifficultyRatioMap();
        Map<Integer, Integer> diffCounts = new LinkedHashMap<>();
        int diffAllocated = 0;
        int[] diffKeys = {1, 2, 3};
        for (int i = 0; i < diffKeys.length; i++) {
            int dk = diffKeys[i];
            int pct = diffRatios.getOrDefault(String.valueOf(dk), i == 0 ? 40 : i == 1 ? 40 : 20);
            if (i == diffKeys.length - 1) {
                diffCounts.put(dk, totalQuestions - diffAllocated);
            } else {
                int count = (int) Math.round(totalQuestions * pct / 100.0);
                diffCounts.put(dk, Math.max(count, 0));
                diffAllocated += count;
            }
        }

        // 获取该分类及其所有子孙分类ID
        List<Long> categoryIds = categoryService.getAllDescendantIds(categoryId);

        // 基于种子的确定性随机选题（按题型+难度）
        Random seededRandom = new Random(seed.hashCode());
        List<Question> selectedQuestions = new ArrayList<>();
        // 先按题型选，尽量满足难度分布
        List<Question> pool = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            pool.addAll(randomSelect(categoryIds, entry.getKey(), entry.getValue(), seededRandom));
        }
        // 按难度重新排序以满足难度比例
        List<Question> easy = new ArrayList<>();
        List<Question> medium = new ArrayList<>();
        List<Question> hard = new ArrayList<>();
        for (Question q : pool) {
            int diff = q.getDifficulty() != null ? q.getDifficulty() : 1;
            if (diff == 1) easy.add(q);
            else if (diff == 3) hard.add(q);
            else medium.add(q);
        }
        // 从各难度池中选取
        for (Question q : easy) {
            if (selectedQuestions.size() < diffCounts.getOrDefault(1, 0) || selectedQuestions.size() < totalQuestions) {
                selectedQuestions.add(q);
            }
        }
        for (Question q : medium) {
            if (selectedQuestions.size() < totalQuestions) selectedQuestions.add(q);
        }
        for (Question q : hard) {
            if (selectedQuestions.size() < totalQuestions) selectedQuestions.add(q);
        }
        // 如果题目不够，从池中补充
        if (selectedQuestions.size() < Math.min(totalQuestions, pool.size())) {
            for (Question q : pool) {
                if (!selectedQuestions.contains(q) && selectedQuestions.size() < totalQuestions) {
                    selectedQuestions.add(q);
                }
            }
        }

        if (selectedQuestions.isEmpty()) {
            return Result.error("该分类下没有足够的题目，请先添加题目");
        }

        // 创建试卷
        Exam exam = new Exam();
        exam.setTitle("随机测试 - " + new Date());
        exam.setCategoryId(categoryId);
        exam.setDuration(duration);
        exam.setType("RANDOM");
        exam.setSeed(seed);
        exam.setStatus(1);
        exam.setDeleted(0);

        BigDecimal totalScore = selectedQuestions.stream()
            .map(Question::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        exam.setTotalScore(totalScore);
        examMapper.insert(exam);

        // 关联题目到试卷
        for (int i = 0; i < selectedQuestions.size(); i++) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(selectedQuestions.get(i).getId());
            eq.setSortOrder(i + 1);
            examQuestionMapper.insert(eq);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("exam", exam);
        result.put("questions", selectedQuestions);
        result.put("questionCount", selectedQuestions.size());
        result.put("seed", seed);
        return Result.success(result);
    }

    /**
     * 基于种子号重新生成相同试卷（相同种子+相同分类→相同题目）
     */
    @Transactional
    public Result<?> generateBySeed(String seed, Long categoryId) {
        if (seed == null || seed.isBlank()) {
            return Result.error("种子号不能为空");
        }
        // 查找该种子下已有的试卷，获取配置信息
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.eq(Exam::getSeed, seed.trim().toUpperCase());
        examWrapper.orderByDesc(Exam::getCreateTime);
        Exam existingExam = examMapper.selectOne(examWrapper);

        ExamConfigDTO configDTO = new ExamConfigDTO();
        if (existingExam != null) {
            configDTO.setCategoryId(existingExam.getCategoryId());
            configDTO.setDuration(existingExam.getDuration());
            // 从题目数推算出总题数
            LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
            eqWrapper.eq(ExamQuestion::getExamId, existingExam.getId());
            Long questionCount = examQuestionMapper.selectCount(eqWrapper);
            configDTO.setTotalQuestions(questionCount != null ? questionCount.intValue() : 20);
        } else if (categoryId != null) {
            // 没有历史记录，使用传入的分类ID和默认配置
            configDTO.setCategoryId(categoryId);
            configDTO.setTotalQuestions(20);
            configDTO.setDuration(60);
        } else {
            return Result.error("找不到该种子号对应的试卷，请同时选择分类");
        }
        configDTO.setSeed(seed.trim().toUpperCase());
        return generateRandomExam(configDTO);
    }

    /**
     * 根据种子号查询已有试卷信息（用于预览种子对应的试卷配置）
     */
    public Result<?> getExamBySeed(String seed) {
        if (seed == null || seed.isBlank()) {
            return Result.error("种子号不能为空");
        }
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.eq(Exam::getSeed, seed.trim().toUpperCase());
        examWrapper.orderByDesc(Exam::getCreateTime);
        Exam exam = examMapper.selectOne(examWrapper);
        if (exam == null) {
            return Result.error("未找到该种子号对应的试卷");
        }

        // 获取该试卷的题目数量
        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, exam.getId());
        Long questionCount = examQuestionMapper.selectCount(eqWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("exam", exam);
        result.put("questionCount", questionCount);
        // 获取分类名称
        Category category = categoryMapper.selectById(exam.getCategoryId());
        result.put("categoryName", category != null ? category.getName() : "");
        return Result.success(result);
    }

    private List<Question> randomSelect(List<Long> categoryIds, String type, int count, Random rng) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getCategoryId, categoryIds);
        wrapper.eq(Question::getType, type);
        List<Question> all = questionMapper.selectList(wrapper);

        if (all.size() <= count) return all;

        // 使用传入的 Random 进行确定性洗牌
        List<Question> copy = new ArrayList<>(all);
        Collections.shuffle(copy, rng);
        return copy.subList(0, count);
    }

    /**
     * 获取试卷详情（含题目列表）
     */
    public Result<?> getExamDetail(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) return Result.error("试卷不存在");

        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, examId);
        eqWrapper.orderByAsc(ExamQuestion::getSortOrder);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(eqWrapper);

        List<Long> questionIds = examQuestions.stream()
            .map(ExamQuestion::getQuestionId)
            .collect(Collectors.toList());

        List<Question> questions = questionIds.isEmpty() ? new ArrayList<>() :
            questionMapper.selectBatchIds(questionIds);

        // 按试卷顺序排列
        Map<Long, Question> questionMap = questions.stream()
            .collect(Collectors.toMap(Question::getId, q -> q));
        List<Question> ordered = examQuestions.stream()
            .map(eq -> questionMap.get(eq.getQuestionId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("exam", exam);
        result.put("questions", ordered);
        return Result.success(result);
    }

    /**
     * 获取试卷列表
     */
    public Result<?> listExams(Long categoryId) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) wrapper.eq(Exam::getCategoryId, categoryId);
        wrapper.orderByDesc(Exam::getCreateTime);
        return Result.success(examMapper.selectList(wrapper));
    }

    /**
     * 创建固定试卷（管理员手动选题）
     */
    @Transactional
    public Result<?> createFixedExam(Exam exam, List<Long> questionIds) {
        exam.setType("FIXED");
        exam.setStatus(1);
        exam.setDeleted(0);

        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        BigDecimal totalScore = questions.stream()
            .map(Question::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        exam.setTotalScore(totalScore);
        examMapper.insert(exam);

        for (int i = 0; i < questionIds.size(); i++) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(questionIds.get(i));
            eq.setSortOrder(i + 1);
            examQuestionMapper.insert(eq);
        }

        return Result.success(exam);
    }

    public Result<?> deleteExam(Long examId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        examQuestionMapper.delete(wrapper);
        examMapper.deleteById(examId);
        return Result.success();
    }
}
