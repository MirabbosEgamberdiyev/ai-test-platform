package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebTestCaseRepositoryPort;
import com.platform.testops.domain.model.WebTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebTestCaseRepository extends JpaRepository<WebTestCaseJpaEntity, UUID> {
    List<WebTestCaseJpaEntity> findByPlanIdOrderByOrderIndexAsc(UUID planId);
    List<WebTestCaseJpaEntity> findByTenantIdOrderByOrderIndexAsc(UUID tenantId);
    void deleteByPlanId(UUID planId);
}

@Component
public class WebTestCaseRepositoryAdapter implements WebTestCaseRepositoryPort {

    private final SpringDataWebTestCaseRepository repository;

    public WebTestCaseRepositoryAdapter(SpringDataWebTestCaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebTestCase save(WebTestCase testCase) {
        WebTestCaseJpaEntity entity = WebTestCaseJpaEntity.fromDomain(testCase);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<WebTestCase> saveAll(List<WebTestCase> testCases) {
        List<WebTestCaseJpaEntity> entities = testCases.stream().map(WebTestCaseJpaEntity::fromDomain).toList();
        return repository.saveAll(entities).stream().map(WebTestCaseJpaEntity::toDomain).toList();
    }

    @Override
    public Optional<WebTestCase> findById(UUID id) {
        return repository.findById(id).map(WebTestCaseJpaEntity::toDomain);
    }

    @Override
    public List<WebTestCase> findByPlanId(UUID planId) {
        return repository.findByPlanIdOrderByOrderIndexAsc(planId)
                .stream()
                .map(WebTestCaseJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<WebTestCase> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByOrderIndexAsc(tenantId.value())
                .stream()
                .map(WebTestCaseJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteByPlanId(UUID planId) {
        repository.deleteByPlanId(planId);
    }
}
