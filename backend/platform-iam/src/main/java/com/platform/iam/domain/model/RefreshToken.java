package com.platform.iam.domain.model;

import com.platform.common.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private final UserId userId;
    private final String tokenHash;
    private boolean revoked;
    private final Instant expiresAt;
    private final Instant createdAt;

    public RefreshToken(UUID id, UserId userId, String tokenHash, boolean revoked, Instant expiresAt, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.revoked = revoked;
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static RefreshToken create(UserId userId, String tokenHash, long validitySeconds) {
        Instant now = Instant.now();
        return new RefreshToken(
                UUID.randomUUID(),
                userId,
                tokenHash,
                false,
                now.plusSeconds(validitySeconds),
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }
}
