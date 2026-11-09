package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebTestProjectRepositoryPort;
import com.platform.testops.domain.model.WebTestProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebTestProjectRepository extends JpaRepository<WebTestProjectJpaEntity, UUID> {
    List<WebTestProjectJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

@Component
public class WebTestProjectRepositoryAdapter implements WebTestProjectRepositoryPort {

    private final SpringDataWebTestProjectRepository repository;

    public WebTestProjectRepositoryAdapter(SpringDataWebTestProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebTestProject save(WebTestProject project) {
        WebTestProjectJpaEntity entity = WebTestProjectJpaEntity.fromDomain(project);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<WebTestProject> findById(UUID id) {
        return repository.findById(id).map(WebTestProjectJpaEntity::toDomain);
    }

    @Override
    public List<WebTestProject> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(WebTestProjectJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
