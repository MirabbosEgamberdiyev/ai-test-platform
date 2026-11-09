package com.platform.automation.application.service;

import com.platform.common.domain.model.ExtractedOption;
import com.platform.automation.application.port.BrowserWorkerPort;
import com.platform.automation.domain.model.BrowserContextSession;
import com.platform.automation.domain.model.BrowserSessionStatus;
import com.platform.automation.domain.model.ExtractedQuestionDom;
import com.platform.automation.domain.model.PracticeSubmissionCommand;
import com.platform.automation.domain.model.SubmissionFeedback;
import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.port.SsrfGuardPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrowserAutomationServiceTest {

    @Mock
    private BrowserWorkerPort browserWorker;
    @Mock
    private SsrfGuardPort ssrfGuard;

    private BrowserAutomationService service;

    @BeforeEach
    void setUp() {
        service = new BrowserAutomationService(browserWorker, ssrfGuard);
    }

    @Test
    @DisplayName("Initiates practice browser after SSRF validation")
    void initiateSessionWithSsrfValidation() {
        UUID sessionId = UUID.randomUUID();
        TenantId tenantId = TenantId.generate();
        String targetUrl = "https://practice.example.com/quiz/1";

        BrowserContextSession mockSession = BrowserContextSession.create(sessionId, tenantId, targetUrl);
        mockSession.updateStatus(BrowserSessionStatus.ACTIVE);

        when(browserWorker.startSession(sessionId, tenantId, targetUrl)).thenReturn(mockSession);

        BrowserContextSession result = service.initiatePracticeBrowser(sessionId, tenantId, targetUrl);

        assertThat(result.getStatus()).isEqualTo(BrowserSessionStatus.ACTIVE);
        verify(ssrfGuard).validateUrl(targetUrl);
        verify(browserWorker).startSession(sessionId, tenantId, targetUrl);
    }

    @Test
    @DisplayName("Extracts question DOM and options")
    void extractQuestion() {
        UUID sessionId = UUID.randomUUID();
        ExtractedQuestionDom expected = new ExtractedQuestionDom(
                "What is Polymorphism?",
                List.of(new ExtractedOption("A", "Ability to take many forms")),
                "MULTIPLE_CHOICE",
                null,
                "<div>HTML</div>"
        );

        when(browserWorker.extractCurrentQuestion(sessionId)).thenReturn(expected);

        ExtractedQuestionDom result = service.extractQuestion(sessionId);

        assertThat(result.questionText()).isEqualTo("What is Polymorphism?");
        assertThat(result.options()).hasSize(1);
    }

    @Test
    @DisplayName("Submits practice answer successfully")
    void submitAnswer() {
        UUID sessionId = UUID.randomUUID();
        PracticeSubmissionCommand cmd = new PracticeSubmissionCommand(sessionId, "A", null);
        SubmissionFeedback feedback = SubmissionFeedback.success("Answer captured");

        when(browserWorker.submitPracticeAnswer(cmd)).thenReturn(feedback);

        SubmissionFeedback actual = service.submitAnswer(cmd);

        assertThat(actual.isSubmitted()).isTrue();
        assertThat(actual.detectedResultText()).isEqualTo("Answer captured");
    }
}
