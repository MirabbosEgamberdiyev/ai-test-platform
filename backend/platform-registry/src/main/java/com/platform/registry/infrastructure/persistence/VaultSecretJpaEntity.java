package com.platform.registry.infrastructure.persistence;

import com.platform.registry.domain.model.VaultSecret;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registry_vault_secrets")
public class VaultSecretJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "integration_id", nullable = false)
    private UUID integrationId;

    @Column(name = "encrypted_payload", nullable = false, columnDefinition = "TEXT")
    private String encryptedPayload;

    @Column(name = "iv", nullable = false, length = 64)
    private String iv;

    @Column(name = "key_version", nullable = false)
    private int keyVersion;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public VaultSecretJpaEntity() {}

    public static VaultSecretJpaEntity fromDomain(VaultSecret domain) {
        VaultSecretJpaEntity entity = new VaultSecretJpaEntity();
        entity.id = domain.getId();
        entity.integrationId = domain.getIntegrationId();
        entity.encryptedPayload = domain.getEncryptedPayload();
        entity.iv = domain.getIv();
        entity.keyVersion = domain.getKeyVersion();
        entity.expiresAt = domain.getExpiresAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public VaultSecret toDomain() {
        return new VaultSecret(
                this.id,
                this.integrationId,
                this.encryptedPayload,
                this.iv,
                this.keyVersion,
                this.expiresAt,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getIntegrationId() { return integrationId; }
    public String getEncryptedPayload() { return encryptedPayload; }
    public String getIv() { return iv; }
    public int getKeyVersion() { return keyVersion; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
