package com.platform.registry.infrastructure.persistence;

import com.platform.registry.application.port.WebsiteRepositoryPort;
import com.platform.registry.domain.model.RegisteredWebsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebsiteRepository extends JpaRepository<RegisteredWebsiteJpaEntity, UUID> {
    Optional<RegisteredWebsiteJpaEntity> findByDomainName(String domainName);
    boolean existsByDomainName(String domainName);
}

@Component
public class WebsiteRepositoryAdapter implements WebsiteRepositoryPort {

    private final SpringDataWebsiteRepository repository;

    public WebsiteRepositoryAdapter(SpringDataWebsiteRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegisteredWebsite save(RegisteredWebsite website) {
        RegisteredWebsiteJpaEntity entity = RegisteredWebsiteJpaEntity.fromDomain(website);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<RegisteredWebsite> findById(UUID id) {
        return repository.findById(id).map(RegisteredWebsiteJpaEntity::toDomain);
    }

    @Override
    public Optional<RegisteredWebsite> findByDomainName(String domainName) {
        return repository.findByDomainName(domainName).map(RegisteredWebsiteJpaEntity::toDomain);
    }

    @Override
    public boolean existsByDomainName(String domainName) {
        return repository.existsByDomainName(domainName);
    }

    @Override
    public List<RegisteredWebsite> findAll() {
        return repository.findAll().stream().map(RegisteredWebsiteJpaEntity::toDomain).toList();
    }
}
