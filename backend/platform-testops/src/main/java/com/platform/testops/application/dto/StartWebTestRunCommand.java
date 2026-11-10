package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StartWebTestRunCommand(
        @NotNull(message = "Plan ID is required")
        UUID planId,

        String browserType, // CHROMIUM, FIREFOX, WEBKIT
        Integer viewportWidth,
        Integer viewportHeight,
        String authType,    // NONE, GOOGLE_TELEGRAM_SSO, BEARER_TOKEN, SESSION_COOKIE, CREDENTIALS
        String authUsername,
        String authPassword,
        String sessionCookie,
        String bearerToken,
        String storageStateJson
) {
    public StartWebTestRunCommand(UUID planId, String browserType, Integer viewportWidth, Integer viewportHeight) {
        this(planId, browserType, viewportWidth, viewportHeight, "NONE", null, null, null, null, null);
    }
}
