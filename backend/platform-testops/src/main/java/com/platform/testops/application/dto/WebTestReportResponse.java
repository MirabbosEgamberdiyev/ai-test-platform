package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebTestReport;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WebTestReportResponse(
        UUID id,
        UUID runId,
        UUID tenantId,
        String websiteName,
        String targetUrl,
        long executionDurationMs,
        double passPercentage,
        int criticalDefectsCount,
        int highDefectsCount,
        int mediumDefectsCount,
        int lowDefectsCount,
        String aiExecutiveSummary,
        String aiRecommendations,
        String jsonReportPath,
        String htmlReportPath,
        List<WebDefectResponse> defects,
        Instant createdAt
) {
    public static WebTestReportResponse fromDomain(WebTestReport domain, List<WebDefectResponse> defects) {
        return new WebTestReportResponse(
                domain.getId(),
                domain.getRunId(),
                domain.getTenantId().value(),
                domain.getWebsiteName(),
                domain.getTargetUrl(),
                domain.getExecutionDurationMs(),
                domain.getPassPercentage(),
                domain.getCriticalDefectsCount(),
                domain.getHighDefectsCount(),
                domain.getMediumDefectsCount(),
                domain.getLowDefectsCount(),
                domain.getAiExecutiveSummary(),
                domain.getAiRecommendations(),
                domain.getJsonReportPath(),
                domain.getHtmlReportPath(),
                defects != null ? defects : List.of(),
                domain.getCreatedAt()
        );
    }
}
