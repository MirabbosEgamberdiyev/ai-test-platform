package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestReport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "web_test_reports")
public class WebTestReportJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "run_id", nullable = false)
    private UUID runId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "website_name", nullable = false, length = 150)
    private String websiteName;

    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    @Column(name = "execution_duration_ms", nullable = false)
    private long executionDurationMs;

    @Column(name = "pass_percentage", nullable = false)
    private double passPercentage;

    @Column(name = "critical_defects_count", nullable = false)
    private int criticalDefectsCount;

    @Column(name = "high_defects_count", nullable = false)
    private int highDefectsCount;

    @Column(name = "medium_defects_count", nullable = false)
    private int mediumDefectsCount;

    @Column(name = "low_defects_count", nullable = false)
    private int lowDefectsCount;

    @Column(name = "ai_executive_summary", columnDefinition = "TEXT")
    private String aiExecutiveSummary;

    @Column(name = "ai_recommendations", columnDefinition = "TEXT")
    private String aiRecommendations;

    @Column(name = "json_report_path", length = 500)
    private String jsonReportPath;

    @Column(name = "html_report_path", length = 500)
    private String htmlReportPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public WebTestReportJpaEntity() {}

    public static WebTestReportJpaEntity fromDomain(WebTestReport domain) {
        WebTestReportJpaEntity entity = new WebTestReportJpaEntity();
        entity.id = domain.getId();
        entity.runId = domain.getRunId();
        entity.tenantId = domain.getTenantId().value();
        entity.websiteName = domain.getWebsiteName();
        entity.targetUrl = domain.getTargetUrl();
        entity.executionDurationMs = domain.getExecutionDurationMs();
        entity.passPercentage = domain.getPassPercentage();
        entity.criticalDefectsCount = domain.getCriticalDefectsCount();
        entity.highDefectsCount = domain.getHighDefectsCount();
        entity.mediumDefectsCount = domain.getMediumDefectsCount();
        entity.lowDefectsCount = domain.getLowDefectsCount();
        entity.aiExecutiveSummary = domain.getAiExecutiveSummary();
        entity.aiRecommendations = domain.getAiRecommendations();
        entity.jsonReportPath = domain.getJsonReportPath();
        entity.htmlReportPath = domain.getHtmlReportPath();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public WebTestReport toDomain() {
        return new WebTestReport(
                this.id,
                this.runId,
                TenantId.of(this.tenantId),
                this.websiteName,
                this.targetUrl,
                this.executionDurationMs,
                this.passPercentage,
                this.criticalDefectsCount,
                this.highDefectsCount,
                this.mediumDefectsCount,
                this.lowDefectsCount,
                this.aiExecutiveSummary,
                this.aiRecommendations,
                this.jsonReportPath,
                this.htmlReportPath,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRunId() { return runId; }
    public void setRunId(UUID runId) { this.runId = runId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getWebsiteName() { return websiteName; }
    public void setWebsiteName(String websiteName) { this.websiteName = websiteName; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public long getExecutionDurationMs() { return executionDurationMs; }
    public void setExecutionDurationMs(long executionDurationMs) { this.executionDurationMs = executionDurationMs; }
    public double getPassPercentage() { return passPercentage; }
    public void setPassPercentage(double passPercentage) { this.passPercentage = passPercentage; }
    public int getCriticalDefectsCount() { return criticalDefectsCount; }
    public void setCriticalDefectsCount(int criticalDefectsCount) { this.criticalDefectsCount = criticalDefectsCount; }
    public int getHighDefectsCount() { return highDefectsCount; }
    public void setHighDefectsCount(int highDefectsCount) { this.highDefectsCount = highDefectsCount; }
    public int getMediumDefectsCount() { return mediumDefectsCount; }
    public void setMediumDefectsCount(int mediumDefectsCount) { this.mediumDefectsCount = mediumDefectsCount; }
    public int getLowDefectsCount() { return lowDefectsCount; }
    public void setLowDefectsCount(int lowDefectsCount) { this.lowDefectsCount = lowDefectsCount; }
    public String getAiExecutiveSummary() { return aiExecutiveSummary; }
    public void setAiExecutiveSummary(String aiExecutiveSummary) { this.aiExecutiveSummary = aiExecutiveSummary; }
    public String getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(String aiRecommendations) { this.aiRecommendations = aiRecommendations; }
    public String getJsonReportPath() { return jsonReportPath; }
    public void setJsonReportPath(String jsonReportPath) { this.jsonReportPath = jsonReportPath; }
    public String getHtmlReportPath() { return htmlReportPath; }
    public void setHtmlReportPath(String htmlReportPath) { this.htmlReportPath = htmlReportPath; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
