package com.platform.testops.domain.model;

import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PracticeSession {
    private final UUID id;
    private final TenantId tenantId;
    private final UserId userId;
    private final UUID integrationId;
    private final String targetUrl;
    private PracticeSessionState state;
    private final PracticeMode practiceMode;
    private int totalQuestions;
    private int answeredQuestions;
    private long version;
    private final Instant startedAt;
    private Instant finishedAt;

    public PracticeSession(UUID id, TenantId tenantId, UserId userId, UUID integrationId, String targetUrl,
                           PracticeSessionState state, PracticeMode practiceMode, int totalQuestions,
                           int answeredQuestions, long version, Instant startedAt, Instant finishedAt) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.userId = Objects.requireNonNull(userId);
        this.integrationId = integrationId;
        this.targetUrl = Objects.requireNonNull(targetUrl);
        this.state = Objects.requireNonNull(state);
        this.practiceMode = Objects.requireNonNull(practiceMode);
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.version = version;
        this.startedAt = Objects.requireNonNull(startedAt);
        this.finishedAt = finishedAt;
    }

    public static PracticeSession start(TenantId tenantId, UserId userId, UUID integrationId, String targetUrl, PracticeMode mode) {
        return new PracticeSession(
                UUID.randomUUID(),
                tenantId,
                userId,
                integrationId,
                targetUrl,
                PracticeSessionState.INITIALIZED,
                mode != null ? mode : PracticeMode.STUDY_ASSISTED,
                0,
                0,
                0L,
                Instant.now(),
                null
        );
    }

    public void markInProgress() {
        if (state == PracticeSessionState.COMPLETED || state == PracticeSessionState.CANCELLED) {
            throw new BusinessRuleViolationException("Cannot resume a terminated practice session");
        }
        this.state = PracticeSessionState.IN_PROGRESS;
    }

    public void recordAnswer() {
        if (state != PracticeSessionState.IN_PROGRESS && state != PracticeSessionState.INITIALIZED) {
            throw new BusinessRuleViolationException("Cannot record answer on inactive practice session. State is: " + state);
        }
        this.answeredQuestions++;
        this.totalQuestions = Math.max(this.totalQuestions, this.answeredQuestions);
    }

    public void complete() {
        if (state == PracticeSessionState.COMPLETED) {
            return;
        }
        this.state = PracticeSessionState.COMPLETED;
        this.finishedAt = Instant.now();
    }

    public void cancel() {
        this.state = PracticeSessionState.CANCELLED;
        this.finishedAt = Instant.now();
    }

    public void fail(String reason) {
        this.state = PracticeSessionState.FAILED;
        this.finishedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UserId getUserId() { return userId; }
    public UUID getIntegrationId() { return integrationId; }
    public String getTargetUrl() { return targetUrl; }
    public PracticeSessionState getState() { return state; }
    public PracticeMode getPracticeMode() { return practiceMode; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getAnsweredQuestions() { return answeredQuestions; }
    public long getVersion() { return version; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }

    public boolean isActive() {
        return state == PracticeSessionState.INITIALIZED || state == PracticeSessionState.IN_PROGRESS;
    }
}
