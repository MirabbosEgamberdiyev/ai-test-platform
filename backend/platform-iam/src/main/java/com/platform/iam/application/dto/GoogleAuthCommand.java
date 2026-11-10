package com.platform.iam.application.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthCommand(
        @NotBlank(message = "Google credential token or auth code is required")
        String credential,
        String clientId,
        String redirectUri
) {}
