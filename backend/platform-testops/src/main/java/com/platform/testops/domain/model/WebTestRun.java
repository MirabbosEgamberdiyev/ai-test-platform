package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebTestRun {
    private final UUID id;
    private final UUID projectId;
    private final UUID planId;
    private final TenantId tenantId;
    private WebTestRunStatus status;
    private String browserType;
    private int viewportWidth;
    private int viewportHeight;
    private Instant startedAt;
    private Instant completedAt;
    private int totalCases;
    private int passedCases;
    private int failedCases;
    private int blockedCases;
    private int skippedCases;
    private String errorSummary;
    private String executionLogs;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public WebTestRun(UUID id, UUID projectId, UUID planId, TenantId tenantId, WebTestRunStatus status,
                      String browserType, int viewportWidth, int viewportHeight, Instant startedAt,
                      Instant completedAt, int totalCases, int passedCases, int failedCases,
                      int blockedCases, int skippedCases, String errorSummary, String executionLogs,
                      long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.planId = Objects.requireNonNull(planId, "planId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.status = status != null ? status : WebTestRunStatus.CREATED;
        this.browserType = browserType != null ? browserType : "CHROMIUM";
        this.viewportWidth = viewportWidth > 0 ? viewportWidth : 1280;
        this.viewportHeight = viewportHeight > 0 ? viewportHeight : 800;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.totalCases = totalCases;
        this.passedCases = passedCases;
        this.failedCases = failedCases;
        this.blockedCases = blockedCases;
        this.skippedCases = skippedCases;
        this.errorSummary = errorSummary;
        this.executionLogs = executionLogs != null ? executionLogs : "";
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static WebTestRun create(UUID projectId, UUID planId, TenantId tenantId, String browserType, int width, int height) {
        Instant now = Instant.now();
        return new WebTestRun(UUID.randomUUID(), projectId, planId, tenantId, WebTestRunStatus.CREATED,
                browserType, width, height, null, null, 0, 0, 0, 0, 0, null, "", 0L, now, now);
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public UUID getPlanId() { return planId; }
    public TenantId getTenantId() { return tenantId; }
    public WebTestRunStatus getStatus() { return status; }
    public String getBrowserType() { return browserType; }
    public int getViewportWidth() { return viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public int getTotalCases() { return totalCases; }
    public int getPassedCases() { return passedCases; }
    public int getFailedCases() { return failedCases; }
    public int getBlockedCases() { return blockedCases; }
    public int getSkippedCases() { return skippedCases; }
    public String getErrorSummary() { return errorSummary; }
    public String getExecutionLogs() { return executionLogs; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void start() {
        this.status = WebTestRunStatus.RUNNING;
        this.startedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void complete(int total, int passed, int failed, int blocked, int skipped, String summary, String logs) {
        this.status = failed > 0 ? WebTestRunStatus.FAILED : WebTestRunStatus.COMPLETED;
        this.totalCases = total;
        this.passedCases = passed;
        this.failedCases = failed;
        this.blockedCases = blocked;
        this.skippedCases = skipped;
        this.errorSummary = summary;
        this.executionLogs = logs;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void fail(String errorMessage, String logs) {
        this.status = WebTestRunStatus.FAILED;
        this.errorSummary = errorMessage;
        this.executionLogs = logs;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void appendLogs(String newLog) {
        this.executionLogs = (this.executionLogs == null ? "" : this.executionLogs) + "\n" + newLog;
        this.updatedAt = Instant.now();
    }

    public void setStatus(WebTestRunStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
