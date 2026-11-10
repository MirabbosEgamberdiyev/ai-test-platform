package com.platform.iam.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordCommand(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}
