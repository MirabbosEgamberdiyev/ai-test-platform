package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateWebTestPlanCommand(
        @NotNull(message = "Project ID is required")
        UUID projectId,

        @NotBlank(message = "Natural language testing goal is required")
        String naturalLanguageGoal,

        String language, // uz-Latn, uz-Cyrl, ru, en
        List<String> scopes // FUNCTIONAL, UI_UX, RESPONSIVE, ACCESSIBILITY, API, SECURITY, PERFORMANCE
) {}
