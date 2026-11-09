package com.platform.ai.domain.model;

import java.util.List;

public record GeneratedTestPlan(
        String naturalLanguageGoal,
        String detectedScope,
        String language,
        String discoverySummary,
        List<GeneratedTestCase> testCases
) {}
