package com.platform.audit.application.service;

import com.platform.audit.application.port.AuditRepositoryPort;
import com.platform.audit.domain.model.AuditRecord;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditService {

    private final AuditRepositoryPort auditRepository;

    public AuditService(AuditRepositoryPort auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logSecurityEvent(TenantId tenantId, UserId actorId, String actionType,
                                 String resourceType, String resourceId, String clientIp, String detailsJson) {
        AuditRecord record = AuditRecord.create(
                tenantId,
                actorId,
                actionType,
                resourceType,
                resourceId,
                clientIp,
                detailsJson
        );
        auditRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<AuditRecord> getAuditTrail(TenantId tenantId, int limit) {
        return auditRepository.findByTenantId(tenantId, limit);
    }
}
