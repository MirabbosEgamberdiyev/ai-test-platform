package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PracticeResult {
    private final UUID id;
    private final UUID sessionId;
    private final TenantId tenantId;
    private final UserId userId;
    private final int totalQuestions;
    private final int correctAnswers;
    private final BigDecimal accuracyPercentage;
    private final long timeSpentSeconds;
    private final String summaryReportJson;
    private final Instant createdAt;

    public PracticeResult(UUID id, UUID sessionId, TenantId tenantId, UserId userId,
                          int totalQuestions, int correctAnswers, BigDecimal accuracyPercentage,
                          long timeSpentSeconds, String summaryReportJson, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.userId = Objects.requireNonNull(userId);
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracyPercentage = Objects.requireNonNull(accuracyPercentage);
        this.timeSpentSeconds = timeSpentSeconds;
        this.summaryReportJson = summaryReportJson;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static PracticeResult calculate(UUID sessionId, TenantId tenantId, UserId userId,
                                           int totalQuestions, int correctAnswers, long durationSeconds, String summaryJson) {
        BigDecimal accuracy = totalQuestions > 0
                ? BigDecimal.valueOf(correctAnswers)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new PracticeResult(
                UUID.randomUUID(),
                sessionId,
                tenantId,
                userId,
                totalQuestions,
                correctAnswers,
                accuracy,
                durationSeconds,
                summaryJson,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public TenantId getTenantId() { return tenantId; }
    public UserId getUserId() { return userId; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public BigDecimal getAccuracyPercentage() { return accuracyPercentage; }
    public long getTimeSpentSeconds() { return timeSpentSeconds; }
    public String getSummaryReportJson() { return summaryReportJson; }
    public Instant getCreatedAt() { return createdAt; }
}
