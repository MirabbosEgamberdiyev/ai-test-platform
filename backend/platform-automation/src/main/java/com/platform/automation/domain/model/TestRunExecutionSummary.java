package com.platform.automation.domain.model;

import java.util.List;

public record TestRunExecutionSummary(
        int totalCases,
        int passedCases,
        int failedCases,
        int blockedCases,
        int skippedCases,
        long durationMs,
        String errorSummary,
        String executionLogs,
        List<ExecutionDefectData> defects
) {}
