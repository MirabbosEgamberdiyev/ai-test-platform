package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.ExecutionMode;
import com.platform.testops.domain.model.SubmissionStatus;
import com.platform.testops.domain.model.UrlSolvingSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "url_solving_sessions")
public class UrlSolvingSessionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "discovery_session_id")
    private UUID discoverySessionId;

    @Column(name = "target_url", nullable = false, length = 1000)
    private String targetUrl;

    @Column(name = "selected_ticket_numbers", nullable = false, columnDefinition = "TEXT")
    private String selectedTicketNumbers;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "execution_mode", nullable = false, length = 50)
    private String executionMode;

    @Column(name = "submission_status", nullable = false, length = 50)
    private String submissionStatus;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "solved_questions_count", nullable = false)
    private int solvedQuestionsCount;

    @Column(name = "verified_questions_count", nullable = false)
    private int verifiedQuestionsCount;

    @Column(name = "review_needed_count", nullable = false)
    private int reviewNeededCount;

    @Column(name = "execution_time_ms")
    private long executionTimeMs;

    @Column(name = "screenshot_path", columnDefinition = "TEXT")
    private String screenshotPath;

    @Column(name = "execution_evidence_json", columnDefinition = "TEXT")
    private String executionEvidenceJson;

    @Column(name = "browser_logs", columnDefinition = "TEXT")
    private String browserLogs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public UrlSolvingSessionJpaEntity() {}

    public static UrlSolvingSessionJpaEntity fromDomain(UrlSolvingSession domain) {
        UrlSolvingSessionJpaEntity entity = new UrlSolvingSessionJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.userId = domain.getUserId().value();
        entity.discoverySessionId = domain.getDiscoverySessionId();
        entity.targetUrl = domain.getTargetUrl();
        entity.selectedTicketNumbers = domain.getSelectedTicketNumbers();
        entity.language = domain.getLanguage();
        entity.status = domain.getStatus();
        entity.executionMode = domain.getExecutionMode().name();
        entity.submissionStatus = domain.getSubmissionStatus().name();
        entity.totalQuestions = domain.getTotalQuestions();
        entity.solvedQuestionsCount = domain.getSolvedQuestionsCount();
        entity.verifiedQuestionsCount = domain.getVerifiedQuestionsCount();
        entity.reviewNeededCount = domain.getReviewNeededCount();
        entity.executionTimeMs = domain.getExecutionTimeMs();
        entity.screenshotPath = domain.getScreenshotPath();
        entity.executionEvidenceJson = domain.getExecutionEvidenceJson();
        entity.browserLogs = domain.getBrowserLogs();
        entity.createdAt = domain.getCreatedAt();
        entity.completedAt = domain.getCompletedAt();
        return entity;
    }

    public UrlSolvingSession toDomain() {
        ExecutionMode mode;
        try {
            mode = ExecutionMode.valueOf(executionMode);
        } catch (Exception e) {
            mode = ExecutionMode.ANSWER_MODE;
        }

        SubmissionStatus subStatus;
        try {
            subStatus = SubmissionStatus.valueOf(submissionStatus);
        } catch (Exception e) {
            subStatus = SubmissionStatus.NOT_STARTED;
        }

        return new UrlSolvingSession(
                id,
                TenantId.of(tenantId),
                UserId.of(userId),
                discoverySessionId,
                targetUrl,
                selectedTicketNumbers,
                language,
                status,
                mode,
                subStatus,
                totalQuestions,
                solvedQuestionsCount,
                verifiedQuestionsCount,
                reviewNeededCount,
                executionTimeMs,
                screenshotPath,
                executionEvidenceJson,
                browserLogs,
                createdAt,
                completedAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getUserId() { return userId; }
    public UUID getDiscoverySessionId() { return discoverySessionId; }
    public String getTargetUrl() { return targetUrl; }
    public String getSelectedTicketNumbers() { return selectedTicketNumbers; }
    public String getLanguage() { return language; }
    public String getStatus() { return status; }
    public String getExecutionMode() { return executionMode; }
    public String getSubmissionStatus() { return submissionStatus; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getSolvedQuestionsCount() { return solvedQuestionsCount; }
    public int getVerifiedQuestionsCount() { return verifiedQuestionsCount; }
    public int getReviewNeededCount() { return reviewNeededCount; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public String getScreenshotPath() { return screenshotPath; }
    public String getExecutionEvidenceJson() { return executionEvidenceJson; }
    public String getBrowserLogs() { return browserLogs; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getCompletedAt() { return completedAt; }
}
