package com.platform.testops.application.dto;

import com.platform.testops.domain.model.PracticeSession;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record PracticeSessionResponse(
        UUID id,
        UUID tenantId,
        UUID userId,
        UUID integrationId,
        String targetUrl,
        String state,
        String practiceMode,
        int totalQuestions,
        int answeredQuestions,
        Instant startedAt,
        Instant finishedAt
) implements Serializable {

    public static PracticeSessionResponse fromDomain(PracticeSession session) {
        return new PracticeSessionResponse(
                session.getId(),
                session.getTenantId().value(),
                session.getUserId().value(),
                session.getIntegrationId(),
                session.getTargetUrl(),
                session.getState().name(),
                session.getPracticeMode().name(),
                session.getTotalQuestions(),
                session.getAnsweredQuestions(),
                session.getStartedAt(),
                session.getFinishedAt()
        );
    }
}
