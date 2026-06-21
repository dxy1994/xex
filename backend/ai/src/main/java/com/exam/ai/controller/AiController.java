package com.exam.ai.controller;

import com.exam.ai.agent.AnswerCheckAgent;
import com.exam.ai.agent.QuestionExplainAgent;
import com.exam.ai.agent.QuestionGenerateAgent;
import com.exam.ai.agent.SubjectAgent;
import com.exam.ai.agent.SubjectAgentRegistry;
import com.exam.ai.dto.AiQuestionResult;
import com.exam.ai.dto.AnswerCheckResult;
import com.exam.ai.dto.CheckAnswerRequest;
import com.exam.ai.dto.ExplainQuestionRequest;
import com.exam.ai.dto.ExplanationResult;
import com.exam.ai.dto.GenerateQuestionRequest;
import com.exam.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI接口控制器 - 支持科目智能路由
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final SubjectAgentRegistry agentRegistry;
    private final QuestionGenerateAgent questionGenerateAgent;
    private final AnswerCheckAgent answerCheckAgent;
    private final QuestionExplainAgent questionExplainAgent;

    /** 生成练习题（根据categoryId自动匹配科目Agent） */
    @PostMapping("/generate-questions")
    public Result<AiQuestionResult.BatchResult> generateQuestions(
            @Valid @RequestBody GenerateQuestionRequest request) {
        SubjectAgent agent = resolveAgent(request.getCategoryId(), request.getCategoryName());
        AiQuestionResult.BatchResult result = (agent != null)
                ? agent.generateQuestions(request)
                : questionGenerateAgent.generateQuestions(request);
        return Result.success(result);
    }

    /** 检查答案（根据subjectName匹配科目Agent） */
    @PostMapping("/check-answer")
    public Result<AnswerCheckResult> checkAnswer(
            @Valid @RequestBody CheckAnswerRequest request) {
        SubjectAgent agent = resolveAgentBySubjectName(request.getSubjectName());
        AnswerCheckResult result = (agent != null)
                ? agent.checkAnswer(request)
                : answerCheckAgent.checkAnswer(request);
        return Result.success(result);
    }

    /** 试题讲解（根据subjectName匹配科目Agent） */
    @PostMapping("/explain-question")
    public Result<ExplanationResult> explainQuestion(
            @Valid @RequestBody ExplainQuestionRequest request) {
        SubjectAgent agent = resolveAgentBySubjectName(request.getSubjectName());
        ExplanationResult result = (agent != null)
                ? agent.explainQuestion(request)
                : questionExplainAgent.explainQuestion(request);
        return Result.success(result);
    }

    /** 流式检查答案（SSE） */
    @PostMapping(value = "/check-answer/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter checkAnswerStream(@Valid @RequestBody CheckAnswerRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L);
        SubjectAgent agent = resolveAgentBySubjectName(request.getSubjectName());
        Flux<String> stream = (agent != null)
                ? agent.checkAnswerStream(request)
                : answerCheckAgent.checkAnswerStream(request);

        stream.subscribe(
                chunk -> sendSseEvent(emitter, chunk),
                error -> completeSseWithError(emitter, error),
                emitter::complete
        );
        return emitter;
    }

    /** 流式试题讲解（SSE） */
    @PostMapping(value = "/explain-question/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter explainQuestionStream(@Valid @RequestBody ExplainQuestionRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L);
        SubjectAgent agent = resolveAgentBySubjectName(request.getSubjectName());
        Flux<String> stream = (agent != null)
                ? agent.explainQuestionStream(request)
                : questionExplainAgent.explainQuestionStream(request);

        stream.subscribe(
                chunk -> sendSseEvent(emitter, chunk),
                error -> completeSseWithError(emitter, error),
                emitter::complete
        );
        return emitter;
    }

    /** 获取所有已注册科目的题型信息 */
    @GetMapping("/subjects")
    public Result<Map<String, String[]>> listSubjects() {
        Map<String, String[]> subjects = new LinkedHashMap<>();
        for (String subject : agentRegistry.getRegisteredSubjects()) {
            SubjectAgent agent = agentRegistry.resolveBySubjectName(subject);
            if (agent != null) {
                subjects.put(subject, agent.getQuestionTypes());
            }
        }
        return Result.success(subjects);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("AI服务正常运行");
    }

    private SubjectAgent resolveAgent(Long categoryId, String categoryName) {
        if (categoryId != null) {
            SubjectAgent agent = agentRegistry.resolveByCategoryId(categoryId);
            if (agent != null) return agent;
        }
        if (categoryName != null && !categoryName.isBlank()) {
            return agentRegistry.resolveBySubjectName(categoryName);
        }
        return null;
    }

    private SubjectAgent resolveAgentBySubjectName(String subjectName) {
        if (subjectName == null || subjectName.isBlank()) return null;
        return agentRegistry.resolveBySubjectName(subjectName);
    }

    private void sendSseEvent(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data));
        } catch (IOException e) {
            log.warn("【SSE】发送事件失败: {}", e.getMessage());
        }
    }

    private void completeSseWithError(SseEmitter emitter, Throwable error) {
        log.error("【SSE】流式输出异常", error);
        try {
            emitter.send(SseEmitter.event().data("[ERROR] " + error.getMessage()));
        } catch (IOException ignored) {
        }
        emitter.completeWithError(error);
    }
}
