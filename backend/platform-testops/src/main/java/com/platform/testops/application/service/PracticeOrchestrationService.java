package com.platform.testops.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.application.service.AIRoutingEngineService;
import com.platform.ai.domain.model.AIAnalysisRequest;
import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.automation.application.service.BrowserAutomationService;
import com.platform.automation.domain.model.ExtractedQuestionDom;
import com.platform.automation.domain.model.PracticeSubmissionCommand;
import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.registry.application.port.SsrfGuardPort;
import com.platform.testops.application.dto.PracticeResultResponse;
import com.platform.testops.application.dto.PracticeSessionResponse;
import com.platform.testops.application.dto.QuestionAnalysisResponse;
import com.platform.testops.application.dto.StartPracticeCommand;
import com.platform.testops.application.dto.SubmitPracticeAnswerCommand;
import com.platform.testops.application.port.OutboxEventRepositoryPort;
import com.platform.testops.application.port.PracticeResultRepositoryPort;
import com.platform.testops.application.port.PracticeSessionRepositoryPort;
import com.platform.testops.application.port.TestQuestionRepositoryPort;
import com.platform.testops.domain.model.OutboxEvent;
import com.platform.testops.domain.model.PracticeResult;
import com.platform.testops.domain.model.PracticeSession;
import com.platform.testops.domain.model.TestQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PracticeOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(PracticeOrchestrationService.class);

    private final PracticeSessionRepositoryPort sessionRepository;
    private final TestQuestionRepositoryPort questionRepository;
    private final PracticeResultRepositoryPort resultRepository;
    private final OutboxEventRepositoryPort outboxRepository;
    private final BrowserAutomationService browserService;
    private final AIRoutingEngineService aiRoutingEngine;
    private final SsrfGuardPort ssrfGuard;
    private final ObjectMapper objectMapper;

    public PracticeOrchestrationService(
            PracticeSessionRepositoryPort sessionRepository,
            TestQuestionRepositoryPort questionRepository,
            PracticeResultRepositoryPort resultRepository,
            OutboxEventRepositoryPort outboxRepository,
            BrowserAutomationService browserService,
            AIRoutingEngineService aiRoutingEngine,
            SsrfGuardPort ssrfGuard,
            ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.outboxRepository = outboxRepository;
        this.browserService = browserService;
        this.aiRoutingEngine = aiRoutingEngine;
        this.ssrfGuard = ssrfGuard;
        this.objectMapper = objectMapper;
    }

    public PracticeSessionResponse startSession(TenantId tenantId, UserId userId, StartPracticeCommand cmd) {
        // Enforce SSRF and policy check upfront
        ssrfGuard.validateUrl(cmd.targetUrl());

        PracticeSession session = PracticeSession.start(
                tenantId,
                userId,
                cmd.integrationId(),
                cmd.targetUrl(),
                cmd.practiceMode()
        );

        PracticeSession saved = sessionRepository.save(session);

        // Initiate browser context asynchronously or synchronously
        try {
            browserService.initiatePracticeBrowser(saved.getId(), tenantId, cmd.targetUrl());
            saved.markInProgress();
            saved = sessionRepository.save(saved);
        } catch (Exception e) {
            log.error("Failed to start browser worker session: {}", e.getMessage());
            saved.fail(e.getMessage());
            sessionRepository.save(saved);
            throw new BusinessRuleViolationException("Could not launch practice browser session: " + e.getMessage());
        }

        return PracticeSessionResponse.fromDomain(saved);
    }

    public QuestionAnalysisResponse analyzeCurrentQuestion(TenantId tenantId, UserId userId, UUID sessionId) {
        PracticeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PracticeSession", sessionId));

        if (!session.isActive()) {
            throw new BusinessRuleViolationException("Practice session is not active. State: " + session.getState());
        }

        // 1. Extract Question from Browser DOM
        ExtractedQuestionDom dom = browserService.extractQuestion(sessionId);

        // 2. Dispatch to AI Engine for multimodal reasoning
        AIAnalysisRequest aiRequest = AIAnalysisRequest.ofMultimodal(
                dom.questionText(),
                dom.options(),
                dom.imageBase64()
        );
        AIAnalysisResult aiResult = aiRoutingEngine.analyze(tenantId, userId, aiRequest);

        // 3. Persist Question Entity
        int nextSequence = session.getAnsweredQuestions() + 1;
        String optionsJson = serializeSafe(dom.options());

        TestQuestion question = TestQuestion.create(
                sessionId,
                nextSequence,
                dom.questionText(),
                dom.questionType(),
                optionsJson,
                null
        );
        TestQuestion savedQuestion = questionRepository.save(question);

        return QuestionAnalysisResponse.of(
                savedQuestion.getId(),
                sessionId,
                nextSequence,
                dom.questionText(),
                dom.options(),
                aiResult
        );
    }

    public PracticeSessionResponse submitAnswer(TenantId tenantId, UserId userId, SubmitPracticeAnswerCommand cmd) {
        PracticeSession session = sessionRepository.findById(cmd.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("PracticeSession", cmd.sessionId()));

        TestQuestion question = questionRepository.findById(cmd.questionId())
                .orElseThrow(() -> new EntityNotFoundException("TestQuestion", cmd.questionId()));

        // Emulate answer in browser sandbox
        browserService.submitAnswer(new PracticeSubmissionCommand(cmd.sessionId(), cmd.selectedOption(), null));

        question.recordUserAnswer(cmd.selectedOption(), true);
        questionRepository.save(question);

        session.recordAnswer();
        PracticeSession updated = sessionRepository.save(session);

        return PracticeSessionResponse.fromDomain(updated);
    }

    public PracticeResultResponse finishSession(TenantId tenantId, UserId userId, UUID sessionId) {
        PracticeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PracticeSession", sessionId));

        session.complete();
        sessionRepository.save(session);

        // Clean up browser resources
        browserService.cleanupSession(sessionId);

        // Calculate Result
        List<TestQuestion> questions = questionRepository.findBySessionId(sessionId);
        int total = questions.size();
        int correct = (int) questions.stream().filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
        long durationSeconds = Duration.between(session.getStartedAt(), Instant.now()).getSeconds();

        PracticeResult result = PracticeResult.calculate(
                sessionId,
                tenantId,
                userId,
                total,
                correct,
                durationSeconds,
                "{\"status\":\"PASSED\",\"analyzedBy\":\"AI_ENGINE\"}"
        );
        PracticeResult savedResult = resultRepository.save(result);

        // Save Transactional Outbox Event (Principle 23 & ADR-007)
        String outboxPayload = serializeSafe(PracticeResultResponse.fromDomain(savedResult));
        OutboxEvent outboxEvent = OutboxEvent.create(
                "PracticeSession",
                sessionId.toString(),
                "PRACTICE_SESSION_COMPLETED",
                outboxPayload
        );
        outboxRepository.save(outboxEvent);

        return PracticeResultResponse.fromDomain(savedResult);
    }

    private String serializeSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
