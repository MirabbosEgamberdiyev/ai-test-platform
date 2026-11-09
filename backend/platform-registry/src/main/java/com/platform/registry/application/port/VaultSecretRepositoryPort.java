package com.platform.registry.application.port;

import com.platform.registry.domain.model.VaultSecret;

import java.util.Optional;
import java.util.UUID;

public interface VaultSecretRepositoryPort {
    VaultSecret save(VaultSecret secret);
    Optional<VaultSecret> findByIntegrationId(UUID integrationId);
    void deleteByIntegrationId(UUID integrationId);
}
