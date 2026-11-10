package com.platform.iam.application.dto;

import com.platform.iam.domain.model.User;

import java.io.Serializable;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID tenantId,
        String email,
        String fullName,
        String role,
        String status,
        String telegramChatId,
        boolean mfaEnabled,
        String authProvider,
        boolean emailVerified
) implements Serializable {

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId().value(),
                user.getTenantId().value(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getTelegramChatId(),
                user.isMfaEnabled(),
                user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL",
                user.isEmailVerified()
        );
    }
}
