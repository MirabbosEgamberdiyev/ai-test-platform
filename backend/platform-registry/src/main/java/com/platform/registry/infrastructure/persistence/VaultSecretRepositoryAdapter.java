package com.platform.registry.infrastructure.persistence;

import com.platform.registry.application.port.VaultSecretRepositoryPort;
import com.platform.registry.domain.model.VaultSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataVaultSecretRepository extends JpaRepository<VaultSecretJpaEntity, UUID> {
    Optional<VaultSecretJpaEntity> findByIntegrationId(UUID integrationId);
    void deleteByIntegrationId(UUID integrationId);
}

@Component
public class VaultSecretRepositoryAdapter implements VaultSecretRepositoryPort {

    private final SpringDataVaultSecretRepository repository;

    public VaultSecretRepositoryAdapter(SpringDataVaultSecretRepository repository) {
        this.repository = repository;
    }

    @Override
    public VaultSecret save(VaultSecret secret) {
        VaultSecretJpaEntity entity = VaultSecretJpaEntity.fromDomain(secret);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<VaultSecret> findByIntegrationId(UUID integrationId) {
        return repository.findByIntegrationId(integrationId).map(VaultSecretJpaEntity::toDomain);
    }

    @Override
    public void deleteByIntegrationId(UUID integrationId) {
        repository.deleteByIntegrationId(integrationId);
    }
}
