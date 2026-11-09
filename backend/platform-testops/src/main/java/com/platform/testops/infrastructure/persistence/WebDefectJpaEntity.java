package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebDefect;
import com.platform.testops.domain.model.WebDefectSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "web_defects")
public class WebDefectJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "run_id", nullable = false)
    private UUID runId;

    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "defect_code", nullable = false, length = 50)
    private String defectCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private WebDefectSeverity severity;

    @Column(name = "reproduction_steps", nullable = false, columnDefinition = "TEXT")
    private String reproductionSteps;

    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "actual_result", nullable = false, columnDefinition = "TEXT")
    private String actualResult;

    @Column(name = "screenshot_path", length = 500)
    private String screenshotPath;

    @Column(name = "console_logs", columnDefinition = "TEXT")
    private String consoleLogs;

    @Column(name = "network_evidence", columnDefinition = "TEXT")
    private String networkEvidence;

    @Column(name = "ai_root_cause", columnDefinition = "TEXT")
    private String aiRootCause;

    @Column(name = "confidence_score", nullable = false)
    private double confidenceScore;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public WebDefectJpaEntity() {}

    public static WebDefectJpaEntity fromDomain(WebDefect domain) {
        WebDefectJpaEntity entity = new WebDefectJpaEntity();
        entity.id = domain.getId();
        entity.runId = domain.getRunId();
        entity.caseId = domain.getCaseId();
        entity.tenantId = domain.getTenantId().value();
        entity.defectCode = domain.getDefectCode();
        entity.title = domain.getTitle();
        entity.severity = domain.getSeverity();
        entity.reproductionSteps = domain.getReproductionSteps();
        entity.expectedResult = domain.getExpectedResult();
        entity.actualResult = domain.getActualResult();
        entity.screenshotPath = domain.getScreenshotPath();
        entity.consoleLogs = domain.getConsoleLogs();
        entity.networkEvidence = domain.getNetworkEvidence();
        entity.aiRootCause = domain.getAiRootCause();
        entity.confidenceScore = domain.getConfidenceScore();
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public WebDefect toDomain() {
        return new WebDefect(
                this.id,
                this.runId,
                this.caseId,
                TenantId.of(this.tenantId),
                this.defectCode,
                this.title,
                this.severity,
                this.reproductionSteps,
                this.expectedResult,
                this.actualResult,
                this.screenshotPath,
                this.consoleLogs,
                this.networkEvidence,
                this.aiRootCause,
                this.confidenceScore,
                this.status,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRunId() { return runId; }
    public void setRunId(UUID runId) { this.runId = runId; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getDefectCode() { return defectCode; }
    public void setDefectCode(String defectCode) { this.defectCode = defectCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public WebDefectSeverity getSeverity() { return severity; }
    public void setSeverity(WebDefectSeverity severity) { this.severity = severity; }
    public String getReproductionSteps() { return reproductionSteps; }
    public void setReproductionSteps(String reproductionSteps) { this.reproductionSteps = reproductionSteps; }
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }
    public String getScreenshotPath() { return screenshotPath; }
    public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }
    public String getConsoleLogs() { return consoleLogs; }
    public void setConsoleLogs(String consoleLogs) { this.consoleLogs = consoleLogs; }
    public String getNetworkEvidence() { return networkEvidence; }
    public void setNetworkEvidence(String networkEvidence) { this.networkEvidence = networkEvidence; }
    public String getAiRootCause() { return aiRootCause; }
    public void setAiRootCause(String aiRootCause) { this.aiRootCause = aiRootCause; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
