package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebDefect {
    private final UUID id;
    private final UUID runId;
    private final UUID caseId;
    private final TenantId tenantId;
    private final String defectCode;
    private final String title;
    private final WebDefectSeverity severity;
    private final String reproductionSteps;
    private final String expectedResult;
    private final String actualResult;
    private final String screenshotPath;
    private final String consoleLogs;
    private final String networkEvidence;
    private final String aiRootCause;
    private final double confidenceScore;
    private String status; // OPEN, RESOLVED
    private final Instant createdAt;

    public WebDefect(UUID id, UUID runId, UUID caseId, TenantId tenantId, String defectCode, String title,
                     WebDefectSeverity severity, String reproductionSteps, String expectedResult,
                     String actualResult, String screenshotPath, String consoleLogs, String networkEvidence,
                     String aiRootCause, double confidenceScore, String status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.runId = Objects.requireNonNull(runId, "runId must not be null");
        this.caseId = caseId;
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.defectCode = Objects.requireNonNull(defectCode, "defectCode must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.severity = severity != null ? severity : WebDefectSeverity.MEDIUM;
        this.reproductionSteps = reproductionSteps != null ? reproductionSteps : "";
        this.expectedResult = expectedResult != null ? expectedResult : "";
        this.actualResult = actualResult != null ? actualResult : "";
        this.screenshotPath = screenshotPath;
        this.consoleLogs = consoleLogs;
        this.networkEvidence = networkEvidence;
        this.aiRootCause = aiRootCause;
        this.confidenceScore = confidenceScore;
        this.status = status != null ? status : "OPEN";
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static WebDefect create(UUID runId, UUID caseId, TenantId tenantId, String defectCode,
                                   String title, WebDefectSeverity severity, String reproductionSteps,
                                   String expectedResult, String actualResult, String screenshotPath,
                                   String consoleLogs, String networkEvidence, String aiRootCause, double confidence) {
        return new WebDefect(UUID.randomUUID(), runId, caseId, tenantId, defectCode, title, severity,
                reproductionSteps, expectedResult, actualResult, screenshotPath, consoleLogs, networkEvidence,
                aiRootCause, confidence, "OPEN", Instant.now());
    }

    public UUID getId() { return id; }
    public UUID getRunId() { return runId; }
    public UUID getCaseId() { return caseId; }
    public TenantId getTenantId() { return tenantId; }
    public String getDefectCode() { return defectCode; }
    public String getTitle() { return title; }
    public WebDefectSeverity getSeverity() { return severity; }
    public String getReproductionSteps() { return reproductionSteps; }
    public String getExpectedResult() { return expectedResult; }
    public String getActualResult() { return actualResult; }
    public String getScreenshotPath() { return screenshotPath; }
    public String getConsoleLogs() { return consoleLogs; }
    public String getNetworkEvidence() { return networkEvidence; }
    public String getAiRootCause() { return aiRootCause; }
    public double getConfidenceScore() { return confidenceScore; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void resolve() {
        this.status = "RESOLVED";
    }
}
