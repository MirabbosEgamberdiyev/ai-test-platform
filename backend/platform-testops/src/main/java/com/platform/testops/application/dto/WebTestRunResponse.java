package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebTestRun;
import com.platform.testops.domain.model.WebTestRunStatus;

import java.time.Instant;
import java.util.UUID;

public record WebTestRunResponse(
        UUID id,
        UUID projectId,
        UUID planId,
        UUID tenantId,
        WebTestRunStatus status,
        String browserType,
        int viewportWidth,
        int viewportHeight,
        Instant startedAt,
        Instant completedAt,
        int totalCases,
        int passedCases,
        int failedCases,
        int blockedCases,
        int skippedCases,
        String errorSummary,
        String executionLogs,
        Instant createdAt
) {
    public static WebTestRunResponse fromDomain(WebTestRun domain) {
        return new WebTestRunResponse(
                domain.getId(),
                domain.getProjectId(),
                domain.getPlanId(),
                domain.getTenantId().value(),
                domain.getStatus(),
                domain.getBrowserType(),
                domain.getViewportWidth(),
                domain.getViewportHeight(),
                domain.getStartedAt(),
                domain.getCompletedAt(),
                domain.getTotalCases(),
                domain.getPassedCases(),
                domain.getFailedCases(),
                domain.getBlockedCases(),
                domain.getSkippedCases(),
                domain.getErrorSummary(),
                domain.getExecutionLogs(),
                domain.getCreatedAt()
        );
    }
}
