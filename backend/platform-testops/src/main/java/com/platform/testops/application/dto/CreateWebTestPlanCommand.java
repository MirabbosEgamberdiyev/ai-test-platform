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
        List<String> scopes, // FUNCTIONAL, UI_UX, RESPONSIVE, ACCESSIBILITY, API, SECURITY, PERFORMANCE
        String authType,     // NONE, GOOGLE_TELEGRAM_SSO, BEARER_TOKEN, SESSION_COOKIE, CREDENTIALS
        String authUsername,
        String authPassword,
        String sessionCookie,
        String bearerToken,
        String storageStateJson
) {
    public CreateWebTestPlanCommand(UUID projectId, String naturalLanguageGoal, String language, List<String> scopes) {
        this(projectId, naturalLanguageGoal, language, scopes, "NONE", null, null, null, null, null);
    }
}
