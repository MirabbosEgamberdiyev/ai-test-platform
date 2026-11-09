package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWebProjectCommand(
        @NotBlank(message = "Project or website name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Target URL is required")
        @Size(max = 500)
        String targetUrl,

        String environment,
        String description,
        String instructions,
        Boolean isAuthorized
) {}
