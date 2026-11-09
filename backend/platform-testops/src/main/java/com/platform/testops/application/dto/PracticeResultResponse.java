package com.platform.testops.application.dto;

import com.platform.testops.domain.model.PracticeResult;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PracticeResultResponse(
        UUID id,
        UUID sessionId,
        UUID tenantId,
        UUID userId,
        int totalQuestions,
        int correctAnswers,
        BigDecimal accuracyPercentage,
        long timeSpentSeconds,
        String summaryReportJson,
        Instant createdAt
) implements Serializable {

    public static PracticeResultResponse fromDomain(PracticeResult result) {
        return new PracticeResultResponse(
                result.getId(),
                result.getSessionId(),
                result.getTenantId().value(),
                result.getUserId().value(),
                result.getTotalQuestions(),
                result.getCorrectAnswers(),
                result.getAccuracyPercentage(),
                result.getTimeSpentSeconds(),
                result.getSummaryReportJson(),
                result.getCreatedAt()
        );
    }
}
