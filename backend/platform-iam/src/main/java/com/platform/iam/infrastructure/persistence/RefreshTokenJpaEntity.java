package com.platform.iam.infrastructure.persistence;

import com.platform.common.domain.model.UserId;
import com.platform.iam.domain.model.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "iam_refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RefreshTokenJpaEntity() {}

    public static RefreshTokenJpaEntity fromDomain(RefreshToken domain) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.id = domain.getId();
        entity.userId = domain.getUserId().value();
        entity.tokenHash = domain.getTokenHash();
        entity.revoked = domain.isRevoked();
        entity.expiresAt = domain.getExpiresAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public RefreshToken toDomain() {
        return new RefreshToken(
                this.id,
                UserId.of(this.userId),
                this.tokenHash,
                this.revoked,
                this.expiresAt,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public boolean isRevoked() { return revoked; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
