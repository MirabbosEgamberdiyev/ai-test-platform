package com.platform.iam.application.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailCommand(
        @NotBlank(message = "Verification token is required")
        String token
) {}
