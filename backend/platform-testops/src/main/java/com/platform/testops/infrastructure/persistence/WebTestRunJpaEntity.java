package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestRun;
import com.platform.testops.domain.model.WebTestRunStatus;
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
@Table(name = "web_test_runs")
public class WebTestRunJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private WebTestRunStatus status;

    @Column(name = "browser_type", nullable = false, length = 50)
    private String browserType;

    @Column(name = "viewport_width", nullable = false)
    private int viewportWidth;

    @Column(name = "viewport_height", nullable = false)
    private int viewportHeight;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "total_cases", nullable = false)
    private int totalCases;

    @Column(name = "passed_cases", nullable = false)
    private int passedCases;

    @Column(name = "failed_cases", nullable = false)
    private int failedCases;

    @Column(name = "blocked_cases", nullable = false)
    private int blockedCases;

    @Column(name = "skipped_cases", nullable = false)
    private int skippedCases;

    @Column(name = "error_summary", columnDefinition = "TEXT")
    private String errorSummary;

    @Column(name = "execution_logs", columnDefinition = "TEXT")
    private String executionLogs;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public WebTestRunJpaEntity() {}

    public static WebTestRunJpaEntity fromDomain(WebTestRun domain) {
        WebTestRunJpaEntity entity = new WebTestRunJpaEntity();
        entity.id = domain.getId();
        entity.projectId = domain.getProjectId();
        entity.planId = domain.getPlanId();
        entity.tenantId = domain.getTenantId().value();
        entity.status = domain.getStatus();
        entity.browserType = domain.getBrowserType();
        entity.viewportWidth = domain.getViewportWidth();
        entity.viewportHeight = domain.getViewportHeight();
        entity.startedAt = domain.getStartedAt();
        entity.completedAt = domain.getCompletedAt();
        entity.totalCases = domain.getTotalCases();
        entity.passedCases = domain.getPassedCases();
        entity.failedCases = domain.getFailedCases();
        entity.blockedCases = domain.getBlockedCases();
        entity.skippedCases = domain.getSkippedCases();
        entity.errorSummary = domain.getErrorSummary();
        entity.executionLogs = domain.getExecutionLogs();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public WebTestRun toDomain() {
        return new WebTestRun(
                this.id,
                this.projectId,
                this.planId,
                TenantId.of(this.tenantId),
                this.status,
                this.browserType,
                this.viewportWidth,
                this.viewportHeight,
                this.startedAt,
                this.completedAt,
                this.totalCases,
                this.passedCases,
                this.failedCases,
                this.blockedCases,
                this.skippedCases,
                this.errorSummary,
                this.executionLogs,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public WebTestRunStatus getStatus() { return status; }
    public void setStatus(WebTestRunStatus status) { this.status = status; }
    public String getBrowserType() { return browserType; }
    public void setBrowserType(String browserType) { this.browserType = browserType; }
    public int getViewportWidth() { return viewportWidth; }
    public void setViewportWidth(int viewportWidth) { this.viewportWidth = viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }
    public void setViewportHeight(int viewportHeight) { this.viewportHeight = viewportHeight; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public int getTotalCases() { return totalCases; }
    public void setTotalCases(int totalCases) { this.totalCases = totalCases; }
    public int getPassedCases() { return passedCases; }
    public void setPassedCases(int passedCases) { this.passedCases = passedCases; }
    public int getFailedCases() { return failedCases; }
    public void setFailedCases(int failedCases) { this.failedCases = failedCases; }
    public int getBlockedCases() { return blockedCases; }
    public void setBlockedCases(int blockedCases) { this.blockedCases = blockedCases; }
    public int getSkippedCases() { return skippedCases; }
    public void setSkippedCases(int skippedCases) { this.skippedCases = skippedCases; }
    public String getErrorSummary() { return errorSummary; }
    public void setErrorSummary(String errorSummary) { this.errorSummary = errorSummary; }
    public String getExecutionLogs() { return executionLogs; }
    public void setExecutionLogs(String executionLogs) { this.executionLogs = executionLogs; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
