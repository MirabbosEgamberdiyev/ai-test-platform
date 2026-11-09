package com.platform.testops.application.dto;

public record WebDashboardStatsResponse(
        long totalProjects,
        long totalWebsites,
        long totalTestRuns,
        long passedTests,
        long failedTests,
        long activeExecutions,
        long criticalDefects,
        double successRatePercentage
) {}
