package com.platform.billing.application.service;

import com.platform.billing.application.port.SubscriptionRepositoryPort;
import com.platform.billing.domain.model.TenantSubscription;
import com.platform.common.domain.model.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepositoryPort subscriptionRepository;

    public SubscriptionService(SubscriptionRepositoryPort subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public TenantSubscription getOrCreateSubscription(TenantId tenantId) {
        return subscriptionRepository.findByTenantId(tenantId)
                .orElseGet(() -> subscriptionRepository.save(TenantSubscription.createDefault(tenantId)));
    }

    public void verifyAndConsumeTokens(TenantId tenantId, long tokens) {
        TenantSubscription sub = getOrCreateSubscription(tenantId);
        sub.checkAndConsumeTokens(tokens);
        subscriptionRepository.save(sub);
    }
}
