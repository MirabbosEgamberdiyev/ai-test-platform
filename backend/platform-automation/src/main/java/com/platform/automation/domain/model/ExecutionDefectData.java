package com.platform.automation.domain.model;

import java.util.UUID;

public record ExecutionDefectData(
        UUID caseId,
        String defectCode,
        String title,
        String severity, // CRITICAL, HIGH, MEDIUM, LOW, INFORMATIONAL
        String reproductionSteps,
        String expectedResult,
        String actualResult,
        String screenshotPath,
        String consoleLogs,
        String networkEvidence,
        String aiRootCause,
        double confidenceScore
) {}
