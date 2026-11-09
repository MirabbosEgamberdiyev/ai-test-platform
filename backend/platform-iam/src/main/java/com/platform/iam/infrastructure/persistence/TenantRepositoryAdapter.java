package com.platform.iam.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.iam.application.port.TenantRepositoryPort;
import com.platform.iam.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataTenantRepository extends JpaRepository<TenantJpaEntity, UUID> {
    Optional<TenantJpaEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}

@Component
public class TenantRepositoryAdapter implements TenantRepositoryPort {

    private final SpringDataTenantRepository repository;

    public TenantRepositoryAdapter(SpringDataTenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = TenantJpaEntity.fromDomain(tenant);
        TenantJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Tenant> findById(TenantId id) {
        return repository.findById(id.value()).map(TenantJpaEntity::toDomain);
    }

    @Override
    public Optional<Tenant> findBySlug(String slug) {
        return repository.findBySlug(slug).map(TenantJpaEntity::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }
}
