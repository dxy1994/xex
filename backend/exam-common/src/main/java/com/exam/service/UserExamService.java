package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.dto.SubmitExamDTO;
import com.exam.entity.Exam;
import com.exam.entity.Question;
import com.exam.entity.UserExam;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.UserExamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserExamService {

    private final UserExamMapper userExamMapper;
    private final ExamMapper examMapper;
    private final QuestionMapper questionMapper;
    private final ObjectMapper objectMapper;

    /**
     * 开始考试（创建答题记录）
     */
    public Result<?> startExam(Long userId, Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) return Result.error("试卷不存在");

        UserExam userExam = new UserExam();
        userExam.setUserId(userId);
        userExam.setExamId(examId);
        userExam.setStatus(0);
        userExam.setStartTime(LocalDateTime.now());
        userExamMapper.insert(userExam);

        return Result.success(userExam);
    }

    /**
     * 提交答卷并自动判分
     */
    @Transactional
    public Result<?> submitExam(SubmitExamDTO submitDTO) {
        UserExam userExam = userExamMapper.selectById(submitDTO.getUserExamId());
        if (userExam == null) return Result.error("答题记录不存在");
        if (userExam.getStatus() == 1) return Result.error("该试卷已提交");

        List<SubmitExamDTO.AnswerItem> answerItems = submitDTO.getAnswers();
        BigDecimal totalScore = BigDecimal.ZERO;
        List<Map<String, Object>> details = new ArrayList<>();

        for (SubmitExamDTO.AnswerItem item : answerItems) {
            Question question = questionMapper.selectById(item.getQuestionId());
            if (question == null) continue;

            boolean isCorrect = false;
            String type = question.getType();
            String userAnswer = item.getAnswer() != null ? item.getAnswer().trim() : "";
            String correctAnswer = question.getAnswer().trim();

            // 自动判分：选择题、判断题、填空题（忽略大小写）
            if ("CHOICE".equals(type) || "JUDGE".equals(type)) {
                isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
            } else if ("FILL".equals(type)) {
                isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
            }
            // 应用题需要人工判分，默认给0分

            if (isCorrect) {
                totalScore = totalScore.add(question.getScore());
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("questionId", question.getId());
            detail.put("type", type);
            detail.put("userAnswer", userAnswer);
            detail.put("correctAnswer", correctAnswer);
            detail.put("isCorrect", isCorrect);
            detail.put("score", isCorrect ? question.getScore() : 0);
            details.add(detail);
        }

        // 更新答题记录
        try {
            userExam.setAnswers(objectMapper.writeValueAsString(details));
        } catch (Exception e) {
            userExam.setAnswers("[]");
        }
        userExam.setScore(totalScore);
        userExam.setStatus(1);
        userExam.setEndTime(LocalDateTime.now());
        userExamMapper.updateById(userExam);

        Map<String, Object> result = new HashMap<>();
        result.put("score", totalScore);
        result.put("details", details);
        return Result.success(result);
    }

    /**
     * 获取用户的考试历史
     */
    public Result<?> getUserExamHistory(Long userId) {
        LambdaQueryWrapper<UserExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserExam::getUserId, userId);
        wrapper.orderByDesc(UserExam::getCreateTime);
        List<UserExam> userExams = userExamMapper.selectList(wrapper);

        List<Map<String, Object>> history = new ArrayList<>();
        for (UserExam ue : userExams) {
            Exam exam = examMapper.selectById(ue.getExamId());
            Map<String, Object> item = new HashMap<>();
            item.put("userExam", ue);
            item.put("exam", exam);
            history.add(item);
        }
        return Result.success(history);
    }

    /**
     * 获取答题详情
     */
    public Result<?> getUserExamDetail(Long userExamId) {
        UserExam userExam = userExamMapper.selectById(userExamId);
        if (userExam == null) return Result.error("记录不存在");

        Exam exam = examMapper.selectById(userExam.getExamId());
        Map<String, Object> result = new HashMap<>();
        result.put("userExam", userExam);
        result.put("exam", exam);

        try {
            if (userExam.getAnswers() != null) {
                List<?> details = objectMapper.readValue(userExam.getAnswers(), List.class);
                result.put("details", details);
            }
        } catch (Exception e) {
            result.put("details", new ArrayList<>());
        }

        return Result.success(result);
    }

    /**
     * 获取所有用户的考试记录（管理员）
     */
    public Result<?> getAllExamRecords(int pageNum, int pageSize) {
        LambdaQueryWrapper<UserExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UserExam::getCreateTime);
        var page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserExam>(pageNum, pageSize);
        return Result.success(userExamMapper.selectPage(page, wrapper));
    }
}
