package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UrlSolvingSession {
    private final UUID id;
    private final TenantId tenantId;
    private final UserId userId;
    private final UUID discoverySessionId;
    private final String targetUrl;
    private final String selectedTicketNumbers;
    private final String language;
    private String status; // PENDING, SOLVING, COMPLETED, FAILED
    private ExecutionMode executionMode;
    private SubmissionStatus submissionStatus;
    private int totalQuestions;
    private int solvedQuestionsCount;
    private int verifiedQuestionsCount;
    private int reviewNeededCount;
    private long executionTimeMs;
    private String screenshotPath;
    private String executionEvidenceJson;
    private String browserLogs;
    private final Instant createdAt;
    private Instant completedAt;
    private List<SolvedQuestion> solvedQuestions;

    public UrlSolvingSession(UUID id, TenantId tenantId, UserId userId, UUID discoverySessionId,
                             String targetUrl, String selectedTicketNumbers, String language,
                             String status, ExecutionMode executionMode, SubmissionStatus submissionStatus,
                             int totalQuestions, int solvedQuestionsCount,
                             int verifiedQuestionsCount, int reviewNeededCount, long executionTimeMs,
                             String screenshotPath, String executionEvidenceJson, String browserLogs,
                             Instant createdAt, Instant completedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.discoverySessionId = discoverySessionId;
        this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must not be null");
        this.selectedTicketNumbers = Objects.requireNonNull(selectedTicketNumbers, "selectedTicketNumbers must not be null");
        this.language = language != null ? language : "uz-Latn";
        this.status = status != null ? status : "PENDING";
        this.executionMode = executionMode != null ? executionMode : ExecutionMode.ANSWER_MODE;
        this.submissionStatus = submissionStatus != null ? submissionStatus : SubmissionStatus.NOT_STARTED;
        this.totalQuestions = totalQuestions;
        this.solvedQuestionsCount = solvedQuestionsCount;
        this.verifiedQuestionsCount = verifiedQuestionsCount;
        this.reviewNeededCount = reviewNeededCount;
        this.executionTimeMs = executionTimeMs;
        this.screenshotPath = screenshotPath;
        this.executionEvidenceJson = executionEvidenceJson;
        this.browserLogs = browserLogs;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.completedAt = completedAt;
        this.solvedQuestions = new ArrayList<>();
    }

    public static UrlSolvingSession create(TenantId tenantId, UserId userId, UUID discoverySessionId,
                                           String targetUrl, String selectedTicketNumbers, String language,
                                           ExecutionMode executionMode) {
        return new UrlSolvingSession(
                UUID.randomUUID(), tenantId, userId, discoverySessionId,
                targetUrl, selectedTicketNumbers, language,
                "PENDING",
                executionMode != null ? executionMode : ExecutionMode.ANSWER_MODE,
                executionMode == ExecutionMode.AUTHORIZED_AUTOMATION_MODE ? SubmissionStatus.WAITING_FOR_APPROVAL : SubmissionStatus.NOT_STARTED,
                0, 0, 0, 0, 0L,
                null, null, null,
                Instant.now(), null
        );
    }

    public void complete(List<SolvedQuestion> questions, long durationMs) {
        this.solvedQuestions = new ArrayList<>(questions);
        this.totalQuestions = questions.size();
        this.solvedQuestionsCount = questions.size();
        this.verifiedQuestionsCount = (int) questions.stream().filter(q -> "VERIFIED".equalsIgnoreCase(q.getVerificationStatus()) || "SOLVED".equalsIgnoreCase(q.getVerificationStatus())).count();
        this.reviewNeededCount = (int) questions.stream().filter(q -> "NEEDS_REVIEW".equalsIgnoreCase(q.getVerificationStatus()) || "AMBIGUOUS".equalsIgnoreCase(q.getVerificationStatus())).count();
        this.executionTimeMs = durationMs;
        this.status = "COMPLETED";
        if (this.executionMode == ExecutionMode.ANSWER_MODE) {
            this.submissionStatus = SubmissionStatus.VERIFIED;
        }
        this.completedAt = Instant.now();
    }

    public void recordAutomationEvidence(String screenshotPath, String evidenceJson, String logs, SubmissionStatus finalStatus) {
        this.screenshotPath = screenshotPath;
        this.executionEvidenceJson = evidenceJson;
        this.browserLogs = logs;
        this.submissionStatus = finalStatus;
        this.completedAt = Instant.now();
    }

    public void fail(String error) {
        this.status = "FAILED";
        this.submissionStatus = SubmissionStatus.FAILED;
        this.completedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UserId getUserId() { return userId; }
    public UUID getDiscoverySessionId() { return discoverySessionId; }
    public String getTargetUrl() { return targetUrl; }
    public String getSelectedTicketNumbers() { return selectedTicketNumbers; }
    public String getLanguage() { return language; }
    public String getStatus() { return status; }
    public ExecutionMode getExecutionMode() { return executionMode; }
    public SubmissionStatus getSubmissionStatus() { return submissionStatus; }
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
    public List<SolvedQuestion> getSolvedQuestions() { return solvedQuestions; }
    public void setSolvedQuestions(List<SolvedQuestion> solvedQuestions) { this.solvedQuestions = solvedQuestions; }
    public void setSubmissionStatus(SubmissionStatus submissionStatus) { this.submissionStatus = submissionStatus; }
    public void setExecutionMode(ExecutionMode executionMode) { this.executionMode = executionMode; }
}
