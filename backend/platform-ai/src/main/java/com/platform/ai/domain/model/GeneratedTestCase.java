package com.platform.ai.domain.model;

import java.util.List;

public record GeneratedTestCase(
        String title,
        String category, // FUNCTIONAL, UI_UX, RESPONSIVE, ACCESSIBILITY, API, SECURITY, PERFORMANCE
        String priority, // CRITICAL, HIGH, MEDIUM, LOW
        String stepsJson,
        String expectedResult,
        int orderIndex
) {}
