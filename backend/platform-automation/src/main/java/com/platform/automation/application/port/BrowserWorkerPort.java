package com.platform.automation.application.port;

import com.platform.automation.domain.model.BrowserContextSession;
import com.platform.automation.domain.model.ExtractedQuestionDom;
import com.platform.automation.domain.model.PracticeSubmissionCommand;
import com.platform.automation.domain.model.SubmissionFeedback;
import com.platform.common.domain.model.TenantId;

import java.util.UUID;

public interface BrowserWorkerPort {
    BrowserContextSession startSession(UUID sessionId, TenantId tenantId, String targetUrl);
    ExtractedQuestionDom extractCurrentQuestion(UUID sessionId);
    SubmissionFeedback submitPracticeAnswer(PracticeSubmissionCommand command);
    void terminateSession(UUID sessionId);
    boolean isSessionActive(UUID sessionId);
}
