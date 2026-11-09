package com.platform.registry.application.dto;

import com.platform.registry.domain.model.IntegrationAuthType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfigureIntegrationCommand(
        @NotNull(message = "Website ID is required")
        UUID websiteId,

        @NotNull(message = "Auth type is required")
        IntegrationAuthType authType,

        String configJson,

        String optionalSecretToVault
) {}
