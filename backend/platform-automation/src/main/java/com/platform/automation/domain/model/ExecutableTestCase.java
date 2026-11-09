package com.platform.automation.domain.model;

import java.util.UUID;

public record ExecutableTestCase(
        UUID caseId,
        String title,
        String category,
        String stepsJson,
        String expectedResult
) {}
