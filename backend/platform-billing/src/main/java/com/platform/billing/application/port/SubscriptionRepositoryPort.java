package com.platform.billing.application.port;

import com.platform.billing.domain.model.TenantSubscription;
import com.platform.common.domain.model.TenantId;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepositoryPort {
    TenantSubscription save(TenantSubscription subscription);
    Optional<TenantSubscription> findById(UUID id);
    Optional<TenantSubscription> findByTenantId(TenantId tenantId);
}
