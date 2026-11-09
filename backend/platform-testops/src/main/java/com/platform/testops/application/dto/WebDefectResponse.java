package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebDefect;
import com.platform.testops.domain.model.WebDefectSeverity;

import java.time.Instant;
import java.util.UUID;

public record WebDefectResponse(
        UUID id,
        UUID runId,
        UUID caseId,
        String defectCode,
        String title,
        WebDefectSeverity severity,
        String reproductionSteps,
        String expectedResult,
        String actualResult,
        String screenshotPath,
        String consoleLogs,
        String networkEvidence,
        String aiRootCause,
        double confidenceScore,
        String status,
        Instant createdAt
) {
    public static WebDefectResponse fromDomain(WebDefect domain) {
        return new WebDefectResponse(
                domain.getId(),
                domain.getRunId(),
                domain.getCaseId(),
                domain.getDefectCode(),
                domain.getTitle(),
                domain.getSeverity(),
                domain.getReproductionSteps(),
                domain.getExpectedResult(),
                domain.getActualResult(),
                domain.getScreenshotPath(),
                domain.getConsoleLogs(),
                domain.getNetworkEvidence(),
                domain.getAiRootCause(),
                domain.getConfidenceScore(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
