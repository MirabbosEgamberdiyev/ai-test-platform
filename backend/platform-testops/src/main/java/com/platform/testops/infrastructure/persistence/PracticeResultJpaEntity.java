package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.PracticeResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "testops_results")
public class PracticeResultJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, unique = true)
    private UUID sessionId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Column(name = "accuracy_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal accuracyPercentage;

    @Column(name = "time_spent_seconds", nullable = false)
    private long timeSpentSeconds;

    @Column(name = "summary_report_json", columnDefinition = "TEXT")
    private String summaryReportJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public PracticeResultJpaEntity() {}

    public static PracticeResultJpaEntity fromDomain(PracticeResult domain) {
        PracticeResultJpaEntity entity = new PracticeResultJpaEntity();
        entity.id = domain.getId();
        entity.sessionId = domain.getSessionId();
        entity.tenantId = domain.getTenantId().value();
        entity.userId = domain.getUserId().value();
        entity.totalQuestions = domain.getTotalQuestions();
        entity.correctAnswers = domain.getCorrectAnswers();
        entity.accuracyPercentage = domain.getAccuracyPercentage();
        entity.timeSpentSeconds = domain.getTimeSpentSeconds();
        entity.summaryReportJson = domain.getSummaryReportJson();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public PracticeResult toDomain() {
        return new PracticeResult(
                this.id,
                this.sessionId,
                TenantId.of(this.tenantId),
                UserId.of(this.userId),
                this.totalQuestions,
                this.correctAnswers,
                this.accuracyPercentage,
                this.timeSpentSeconds,
                this.summaryReportJson,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public UUID getTenantId() { return tenantId; }
    public UUID getUserId() { return userId; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public BigDecimal getAccuracyPercentage() { return accuracyPercentage; }
    public long getTimeSpentSeconds() { return timeSpentSeconds; }
    public String getSummaryReportJson() { return summaryReportJson; }
    public Instant getCreatedAt() { return createdAt; }
}
