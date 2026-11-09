package com.platform.iam.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterTenantCommand(
        @NotBlank(message = "Tenant name is required")
        @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
        String tenantName,

        @NotBlank(message = "Tenant slug is required")
        @Size(min = 2, max = 50, message = "Tenant slug must be between 2 and 50 characters")
        String tenantSlug,

        @NotBlank(message = "Admin full name is required")
        String adminFullName,

        @NotBlank(message = "Admin email is required")
        @Email(message = "Valid email is required")
        String adminEmail,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
        String adminPassword
) {}
