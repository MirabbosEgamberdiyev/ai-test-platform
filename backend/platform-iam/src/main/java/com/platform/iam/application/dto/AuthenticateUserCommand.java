package com.platform.iam.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateUserCommand(
        @NotBlank(message = "Username or email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}
