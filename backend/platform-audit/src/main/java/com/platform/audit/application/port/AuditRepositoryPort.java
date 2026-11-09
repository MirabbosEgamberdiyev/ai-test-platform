package com.platform.audit.application.port;

import com.platform.audit.domain.model.AuditRecord;
import com.platform.common.domain.model.TenantId;

import java.util.List;

public interface AuditRepositoryPort {
    AuditRecord save(AuditRecord record);
    List<AuditRecord> findByTenantId(TenantId tenantId, int limit);
}
