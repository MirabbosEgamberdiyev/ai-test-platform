package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWebProjectCommand(
        @NotBlank(message = "Project or website name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Target URL is required")
        @Size(max = 500)
        String targetUrl,

        String environment, // DEVELOPMENT, STAGING, PRODUCTION
        String description,
        String instructions,
        Boolean isAuthorized
) {}
