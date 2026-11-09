package com.platform.registry.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class VaultSecret {
    private final UUID id;
    private final UUID integrationId;
    private final String encryptedPayload;
    private final String iv;
    private final int keyVersion;
    private final Instant expiresAt;
    private final Instant createdAt;

    public VaultSecret(UUID id, UUID integrationId, String encryptedPayload, String iv, int keyVersion, Instant expiresAt, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.integrationId = Objects.requireNonNull(integrationId);
        this.encryptedPayload = Objects.requireNonNull(encryptedPayload);
        this.iv = Objects.requireNonNull(iv);
        this.keyVersion = keyVersion;
        this.expiresAt = expiresAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static VaultSecret create(UUID integrationId, String encryptedPayload, String iv, int keyVersion, Instant expiresAt) {
        return new VaultSecret(
                UUID.randomUUID(),
                integrationId,
                encryptedPayload,
                iv,
                keyVersion,
                expiresAt,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getIntegrationId() { return integrationId; }
    public String getEncryptedPayload() { return encryptedPayload; }
    public String getIv() { return iv; }
    public int getKeyVersion() { return keyVersion; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
