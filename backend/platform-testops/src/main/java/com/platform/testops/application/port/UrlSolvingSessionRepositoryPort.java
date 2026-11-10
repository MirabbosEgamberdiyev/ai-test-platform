package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.SolvedQuestion;
import com.platform.testops.domain.model.UrlSolvingSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UrlSolvingSessionRepositoryPort {
    UrlSolvingSession save(UrlSolvingSession session);
    Optional<UrlSolvingSession> findById(UUID id);
    List<UrlSolvingSession> findByTenantId(TenantId tenantId);
    List<UrlSolvingSession> findByUserId(UserId userId);
    void saveQuestions(List<SolvedQuestion> questions);
    List<SolvedQuestion> findQuestionsBySessionId(UUID sessionId);
}
