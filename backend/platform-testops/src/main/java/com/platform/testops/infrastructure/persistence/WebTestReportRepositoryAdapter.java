package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebTestReportRepositoryPort;
import com.platform.testops.domain.model.WebTestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebTestReportRepository extends JpaRepository<WebTestReportJpaEntity, UUID> {
    Optional<WebTestReportJpaEntity> findByRunId(UUID runId);
    List<WebTestReportJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

@Component
public class WebTestReportRepositoryAdapter implements WebTestReportRepositoryPort {

    private final SpringDataWebTestReportRepository repository;

    public WebTestReportRepositoryAdapter(SpringDataWebTestReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebTestReport save(WebTestReport report) {
        WebTestReportJpaEntity entity = WebTestReportJpaEntity.fromDomain(report);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<WebTestReport> findById(UUID id) {
        return repository.findById(id).map(WebTestReportJpaEntity::toDomain);
    }

    @Override
    public Optional<WebTestReport> findByRunId(UUID runId) {
        return repository.findByRunId(runId).map(WebTestReportJpaEntity::toDomain);
    }

    @Override
    public List<WebTestReport> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(WebTestReportJpaEntity::toDomain)
                .toList();
    }
}
