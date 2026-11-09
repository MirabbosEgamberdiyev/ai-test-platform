package com.platform.testops.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.application.service.AIRoutingEngineService;
import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.automation.application.service.BrowserAutomationService;
import com.platform.automation.domain.model.ExtractedQuestionDom;
import com.platform.automation.domain.model.SubmissionFeedback;
import com.platform.common.domain.model.ConfidenceScore;
import com.platform.common.domain.model.ExtractedOption;
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
import com.platform.testops.domain.model.PracticeMode;
import com.platform.testops.domain.model.PracticeResult;
import com.platform.testops.domain.model.PracticeSession;
import com.platform.testops.domain.model.PracticeSessionState;
import com.platform.testops.domain.model.TestQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeOrchestrationServiceTest {

    @Mock
    private PracticeSessionRepositoryPort sessionRepository;
    @Mock
    private TestQuestionRepositoryPort questionRepository;
    @Mock
    private PracticeResultRepositoryPort resultRepository;
    @Mock
    private OutboxEventRepositoryPort outboxRepository;
    @Mock
    private BrowserAutomationService browserService;
    @Mock
    private AIRoutingEngineService aiRoutingEngine;
    @Mock
    private SsrfGuardPort ssrfGuard;

    private PracticeOrchestrationService orchestrationService;

    @BeforeEach
    void setUp() {
        orchestrationService = new PracticeOrchestrationService(
                sessionRepository,
                questionRepository,
                resultRepository,
                outboxRepository,
                browserService,
                aiRoutingEngine,
                ssrfGuard,
                new ObjectMapper()
        );
    }

    @Test
    @DisplayName("Starts practice session, performs SSRF check and starts browser session")
    void startPracticeSessionSuccess() {
        TenantId tenantId = TenantId.generate();
        UserId userId = UserId.generate();
        StartPracticeCommand cmd = new StartPracticeCommand(null, "https://allowed.practice.com/test", PracticeMode.STUDY_ASSISTED);

        when(sessionRepository.save(any(PracticeSession.class))).thenAnswer(inv -> inv.getArgument(0));

        PracticeSessionResponse response = orchestrationService.startSession(tenantId, userId, cmd);

        assertThat(response).isNotNull();
        assertThat(response.targetUrl()).isEqualTo(cmd.targetUrl());
        assertThat(response.state()).isEqualTo(PracticeSessionState.IN_PROGRESS.name());

        verify(ssrfGuard).validateUrl(cmd.targetUrl());
        verify(browserService).initiatePracticeBrowser(any(), any(), any());
    }

    @Test
    @DisplayName("Extracts question DOM, runs AI analysis, and persists test question")
    void analyzeQuestionFlow() {
        TenantId tenantId = TenantId.generate();
        UserId userId = UserId.generate();
        UUID sessionId = UUID.randomUUID();

        PracticeSession session = PracticeSession.start(tenantId, userId, null, "https://example.com", PracticeMode.STUDY_ASSISTED);
        session.markInProgress();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        ExtractedQuestionDom dom = new ExtractedQuestionDom(
                "What is the time complexity of binary search?",
                List.of(new ExtractedOption("A", "O(log n)"), new ExtractedOption("B", "O(n)")),
                "MULTIPLE_CHOICE",
                null,
                "<div>HTML</div>"
        );
        when(browserService.extractQuestion(sessionId)).thenReturn(dom);

        AIAnalysisResult aiResult = new AIAnalysisResult(
                "A", "Binary search divides the search space in half each iteration",
                ConfidenceScore.of(0.98), 40, 25, 120, "GEMINI", "gemini-1.5-pro"
        );
        when(aiRoutingEngine.analyze(any(), any(), any())).thenReturn(aiResult);
        when(questionRepository.save(any(TestQuestion.class))).thenAnswer(inv -> inv.getArgument(0));

        QuestionAnalysisResponse response = orchestrationService.analyzeCurrentQuestion(tenantId, userId, sessionId);

        assertThat(response).isNotNull();
        assertThat(response.questionText()).isEqualTo(dom.questionText());
        assertThat(response.predictedAnswer()).isEqualTo("A");
        assertThat(response.confidenceScore()).isEqualTo(0.98);

        verify(questionRepository).save(any(TestQuestion.class));
    }

    @Test
    @DisplayName("Submits answer and advances practice session counter")
    void submitAnswerFlow() {
        TenantId tenantId = TenantId.generate();
        UserId userId = UserId.generate();
        UUID sessionId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        PracticeSession session = PracticeSession.start(tenantId, userId, null, "https://example.com", PracticeMode.STUDY_ASSISTED);
        session.markInProgress();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        TestQuestion question = TestQuestion.create(sessionId, 1, "Question?", "MULTIPLE_CHOICE", "[]", null);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(sessionRepository.save(any(PracticeSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(browserService.submitAnswer(any())).thenReturn(SubmissionFeedback.success("OK"));

        SubmitPracticeAnswerCommand cmd = new SubmitPracticeAnswerCommand(sessionId, questionId, "A");
        PracticeSessionResponse response = orchestrationService.submitAnswer(tenantId, userId, cmd);

        assertThat(response.answeredQuestions()).isEqualTo(1);
        verify(questionRepository).save(any(TestQuestion.class));
    }

    @Test
    @DisplayName("Finishes practice session, calculates score, and generates Outbox event")
    void finishPracticeFlow() {
        TenantId tenantId = TenantId.generate();
        UserId userId = UserId.generate();
        UUID sessionId = UUID.randomUUID();

        PracticeSession session = PracticeSession.start(tenantId, userId, null, "https://example.com", PracticeMode.STUDY_ASSISTED);
        session.markInProgress();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        TestQuestion q1 = TestQuestion.create(sessionId, 1, "Q1", "MULTIPLE_CHOICE", "[]", null);
        q1.recordUserAnswer("A", true);
        TestQuestion q2 = TestQuestion.create(sessionId, 2, "Q2", "MULTIPLE_CHOICE", "[]", null);
        q2.recordUserAnswer("B", true);

        when(questionRepository.findBySessionId(sessionId)).thenReturn(List.of(q1, q2));
        when(resultRepository.save(any(PracticeResult.class))).thenAnswer(inv -> inv.getArgument(0));

        PracticeResultResponse resultResponse = orchestrationService.finishSession(tenantId, userId, sessionId);

        assertThat(resultResponse).isNotNull();
        assertThat(resultResponse.totalQuestions()).isEqualTo(2);
        assertThat(resultResponse.correctAnswers()).isEqualTo(2);
        assertThat(resultResponse.accuracyPercentage()).isEqualTo(new BigDecimal("100.00"));

        verify(browserService).cleanupSession(sessionId);
        verify(outboxRepository).save(any());
    }
}
