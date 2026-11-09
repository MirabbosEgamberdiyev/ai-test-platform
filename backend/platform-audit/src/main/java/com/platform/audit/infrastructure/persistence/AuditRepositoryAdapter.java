package com.platform.audit.infrastructure.persistence;

import com.platform.audit.application.port.AuditRepositoryPort;
import com.platform.audit.domain.model.AuditRecord;
import com.platform.common.domain.model.TenantId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface SpringDataAuditRepository extends JpaRepository<AuditJpaEntity, UUID> {
    List<AuditJpaEntity> findByTenantIdOrderByTimestampDesc(UUID tenantId, PageRequest pageRequest);
}

@Component
public class AuditRepositoryAdapter implements AuditRepositoryPort {

    private final SpringDataAuditRepository repository;

    public AuditRepositoryAdapter(SpringDataAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditRecord save(AuditRecord record) {
        AuditJpaEntity entity = AuditJpaEntity.fromDomain(record);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<AuditRecord> findByTenantId(TenantId tenantId, int limit) {
        return repository.findByTenantIdOrderByTimestampDesc(tenantId.value(), PageRequest.of(0, limit)).stream()
                .map(AuditJpaEntity::toDomain)
                .toList();
    }
}
