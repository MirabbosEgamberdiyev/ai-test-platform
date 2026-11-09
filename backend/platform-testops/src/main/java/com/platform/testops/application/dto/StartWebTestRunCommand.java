package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StartWebTestRunCommand(
        @NotNull(message = "Plan ID is required")
        UUID planId,

        String browserType, // CHROMIUM, FIREFOX, WEBKIT
        Integer viewportWidth,
        Integer viewportHeight
) {}
