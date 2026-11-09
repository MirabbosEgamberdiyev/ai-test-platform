package com.platform.iam.application.port;

import com.platform.common.domain.model.UserId;
import com.platform.iam.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllForUser(UserId userId);
}
