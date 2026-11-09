package com.platform.automation.application.service;

import com.platform.automation.application.port.BrowserWorkerPort;
import com.platform.automation.domain.model.BrowserContextSession;
import com.platform.automation.domain.model.ExtractedQuestionDom;
import com.platform.automation.domain.model.PracticeSubmissionCommand;
import com.platform.automation.domain.model.SubmissionFeedback;
import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.port.SsrfGuardPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BrowserAutomationService {

    private final BrowserWorkerPort browserWorker;
    private final SsrfGuardPort ssrfGuard;

    public BrowserAutomationService(BrowserWorkerPort browserWorker, SsrfGuardPort ssrfGuard) {
        this.browserWorker = browserWorker;
        this.ssrfGuard = ssrfGuard;
    }

    public BrowserContextSession initiatePracticeBrowser(UUID sessionId, TenantId tenantId, String targetUrl) {
        // Strict SSRF and non-circumvention security verification
        ssrfGuard.validateUrl(targetUrl);
        return browserWorker.startSession(sessionId, tenantId, targetUrl);
    }

    public ExtractedQuestionDom extractQuestion(UUID sessionId) {
        return browserWorker.extractCurrentQuestion(sessionId);
    }

    public SubmissionFeedback submitAnswer(PracticeSubmissionCommand command) {
        return browserWorker.submitPracticeAnswer(command);
    }

    public void cleanupSession(UUID sessionId) {
        browserWorker.terminateSession(sessionId);
    }
}
