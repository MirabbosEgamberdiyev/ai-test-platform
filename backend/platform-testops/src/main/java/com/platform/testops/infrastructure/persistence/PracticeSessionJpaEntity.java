package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.PracticeMode;
import com.platform.testops.domain.model.PracticeSession;
import com.platform.testops.domain.model.PracticeSessionState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "testops_sessions")
public class PracticeSessionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "integration_id")
    private UUID integrationId;

    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 30)
    private PracticeSessionState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "practice_mode", nullable = false, length = 30)
    private PracticeMode practiceMode;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "answered_questions", nullable = false)
    private int answeredQuestions;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    public PracticeSessionJpaEntity() {}

    public static PracticeSessionJpaEntity fromDomain(PracticeSession domain) {
        PracticeSessionJpaEntity entity = new PracticeSessionJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.userId = domain.getUserId().value();
        entity.integrationId = domain.getIntegrationId();
        entity.targetUrl = domain.getTargetUrl();
        entity.state = domain.getState();
        entity.practiceMode = domain.getPracticeMode();
        entity.totalQuestions = domain.getTotalQuestions();
        entity.answeredQuestions = domain.getAnsweredQuestions();
        entity.version = domain.getVersion();
        entity.startedAt = domain.getStartedAt();
        entity.finishedAt = domain.getFinishedAt();
        return entity;
    }

    public PracticeSession toDomain() {
        return new PracticeSession(
                this.id,
                TenantId.of(this.tenantId),
                UserId.of(this.userId),
                this.integrationId,
                this.targetUrl,
                this.state,
                this.practiceMode,
                this.totalQuestions,
                this.answeredQuestions,
                this.version != null ? this.version : 0L,
                this.startedAt,
                this.finishedAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getUserId() { return userId; }
    public UUID getIntegrationId() { return integrationId; }
    public String getTargetUrl() { return targetUrl; }
    public PracticeSessionState getState() { return state; }
    public PracticeMode getPracticeMode() { return practiceMode; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getAnsweredQuestions() { return answeredQuestions; }
    public Long getVersion() { return version; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
}
