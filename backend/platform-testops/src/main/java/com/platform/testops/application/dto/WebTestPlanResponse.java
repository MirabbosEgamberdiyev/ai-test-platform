package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebTestPlan;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WebTestPlanResponse(
        UUID id,
        UUID projectId,
        UUID tenantId,
        String naturalLanguageGoal,
        String detectedScope,
        String language,
        String status,
        String discoverySummary,
        List<WebTestCaseDto> testCases,
        Instant createdAt,
        Instant updatedAt
) {
    public static WebTestPlanResponse fromDomain(WebTestPlan plan, List<WebTestCaseDto> cases) {
        return new WebTestPlanResponse(
                plan.getId(),
                plan.getProjectId(),
                plan.getTenantId().value(),
                plan.getNaturalLanguageGoal(),
                plan.getDetectedScope(),
                plan.getLanguage(),
                plan.getStatus(),
                plan.getDiscoverySummary(),
                cases != null ? cases : List.of(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
