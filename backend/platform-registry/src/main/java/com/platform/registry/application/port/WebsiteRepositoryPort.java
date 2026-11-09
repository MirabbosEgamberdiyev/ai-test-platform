package com.platform.registry.application.port;

import com.platform.registry.domain.model.RegisteredWebsite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebsiteRepositoryPort {
    RegisteredWebsite save(RegisteredWebsite website);
    Optional<RegisteredWebsite> findById(UUID id);
    Optional<RegisteredWebsite> findByDomainName(String domainName);
    boolean existsByDomainName(String domainName);
    List<RegisteredWebsite> findAll();
}
