package com.platform.iam.application.port;

import com.platform.iam.domain.model.User;

public interface TokenProviderPort {
    String generateAccessToken(User user);
    String generateRefreshToken();
    String hashToken(String rawToken);
    boolean validateAccessToken(String token);
    String extractUsername(String token);
}
