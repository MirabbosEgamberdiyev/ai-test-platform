package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;

public record DiscoverUrlCommand(
        @NotBlank(message = "Target URL is required")
        String targetUrl
) {}
