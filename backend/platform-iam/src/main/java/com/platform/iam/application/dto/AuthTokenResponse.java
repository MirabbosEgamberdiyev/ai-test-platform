package com.platform.iam.application.dto;

import java.io.Serializable;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) implements Serializable {
    public static AuthTokenResponse of(String accessToken, String refreshToken, long expiresInSeconds, UserResponse user) {
        return new AuthTokenResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, user);
    }
}
