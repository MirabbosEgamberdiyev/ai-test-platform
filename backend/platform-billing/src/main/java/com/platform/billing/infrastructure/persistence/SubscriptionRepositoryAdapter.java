package com.platform.billing.infrastructure.persistence;

import com.platform.billing.application.port.SubscriptionRepositoryPort;
import com.platform.billing.domain.model.TenantSubscription;
import com.platform.common.domain.model.TenantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, UUID> {
    Optional<SubscriptionJpaEntity> findByTenantId(UUID tenantId);
}

@Component
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    private final SpringDataSubscriptionRepository repository;

    public SubscriptionRepositoryAdapter(SpringDataSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public TenantSubscription save(TenantSubscription subscription) {
        SubscriptionJpaEntity entity = SubscriptionJpaEntity.fromDomain(subscription);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<TenantSubscription> findById(UUID id) {
        return repository.findById(id).map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public Optional<TenantSubscription> findByTenantId(TenantId tenantId) {
        return repository.findByTenantId(tenantId.value()).map(SubscriptionJpaEntity::toDomain);
    }
}
