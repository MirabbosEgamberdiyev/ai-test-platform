package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebTestReport {
    private final UUID id;
    private final UUID runId;
    private final TenantId tenantId;
    private final String websiteName;
    private final String targetUrl;
    private final long executionDurationMs;
    private final double passPercentage;
    private final int criticalDefectsCount;
    private final int highDefectsCount;
    private final int mediumDefectsCount;
    private final int lowDefectsCount;
    private final String aiExecutiveSummary;
    private final String aiRecommendations;
    private String jsonReportPath;
    private String htmlReportPath;
    private final Instant createdAt;

    public WebTestReport(UUID id, UUID runId, TenantId tenantId, String websiteName, String targetUrl,
                         long executionDurationMs, double passPercentage, int criticalDefectsCount,
                         int highDefectsCount, int mediumDefectsCount, int lowDefectsCount,
                         String aiExecutiveSummary, String aiRecommendations,
                         String jsonReportPath, String htmlReportPath, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.runId = Objects.requireNonNull(runId, "runId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.websiteName = Objects.requireNonNull(websiteName, "websiteName must not be null");
        this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must not be null");
        this.executionDurationMs = executionDurationMs;
        this.passPercentage = passPercentage;
        this.criticalDefectsCount = criticalDefectsCount;
        this.highDefectsCount = highDefectsCount;
        this.mediumDefectsCount = mediumDefectsCount;
        this.lowDefectsCount = lowDefectsCount;
        this.aiExecutiveSummary = aiExecutiveSummary;
        this.aiRecommendations = aiRecommendations;
        this.jsonReportPath = jsonReportPath;
        this.htmlReportPath = htmlReportPath;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static WebTestReport create(UUID runId, TenantId tenantId, String websiteName, String targetUrl,
                                       long durationMs, double passPercentage, int critical, int high,
                                       int medium, int low, String aiSummary, String aiRecommendations) {
        return new WebTestReport(UUID.randomUUID(), runId, tenantId, websiteName, targetUrl,
                durationMs, passPercentage, critical, high, medium, low, aiSummary, aiRecommendations,
                null, null, Instant.now());
    }

    public UUID getId() { return id; }
    public UUID getRunId() { return runId; }
    public TenantId getTenantId() { return tenantId; }
    public String getWebsiteName() { return websiteName; }
    public String getTargetUrl() { return targetUrl; }
    public long getExecutionDurationMs() { return executionDurationMs; }
    public double getPassPercentage() { return passPercentage; }
    public int getCriticalDefectsCount() { return criticalDefectsCount; }
    public int getHighDefectsCount() { return highDefectsCount; }
    public int getMediumDefectsCount() { return mediumDefectsCount; }
    public int getLowDefectsCount() { return lowDefectsCount; }
    public String getAiExecutiveSummary() { return aiExecutiveSummary; }
    public String getAiRecommendations() { return aiRecommendations; }
    public String getJsonReportPath() { return jsonReportPath; }
    public String getHtmlReportPath() { return htmlReportPath; }
    public Instant getCreatedAt() { return createdAt; }

    public void setReportPaths(String jsonPath, String htmlPath) {
        this.jsonReportPath = jsonPath;
        this.htmlReportPath = htmlPath;
    }
}
