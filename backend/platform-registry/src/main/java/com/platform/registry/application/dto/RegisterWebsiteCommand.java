package com.platform.registry.application.dto;

import com.platform.registry.domain.model.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterWebsiteCommand(
        @NotBlank(message = "Domain name is required")
        @Pattern(regexp = "^(?i)[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,10}$", message = "Invalid domain name format")
        String domainName,

        @NotBlank(message = "Display name is required")
        String displayName,

        @NotBlank(message = "Base URL is required")
        String baseUrl,

        PlatformType platformType
) {}
