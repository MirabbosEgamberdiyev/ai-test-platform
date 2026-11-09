package com.platform.registry.application.service;

import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.registry.application.dto.RegisterWebsiteCommand;
import com.platform.registry.application.dto.WebsiteResponse;
import com.platform.registry.application.port.SsrfGuardPort;
import com.platform.registry.application.port.WebsiteRepositoryPort;
import com.platform.registry.domain.model.RegisteredWebsite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WebsiteRegistryService {

    private final WebsiteRepositoryPort websiteRepository;
    private final SsrfGuardPort ssrfGuard;

    public WebsiteRegistryService(WebsiteRepositoryPort websiteRepository, SsrfGuardPort ssrfGuard) {
        this.websiteRepository = websiteRepository;
        this.ssrfGuard = ssrfGuard;
    }

    public WebsiteResponse registerWebsite(RegisterWebsiteCommand cmd) {
        String normalizedDomain = cmd.domainName().toLowerCase().trim();
        if (websiteRepository.existsByDomainName(normalizedDomain)) {
            throw new BusinessRuleViolationException("Website with domain '" + normalizedDomain + "' already registered");
        }

        // Validate URL and ensure domain is not a forbidden proctoring or private IP
        ssrfGuard.validateUrl(cmd.baseUrl());

        RegisteredWebsite site = RegisteredWebsite.create(
                normalizedDomain,
                cmd.displayName(),
                cmd.baseUrl(),
                cmd.platformType()
        );

        RegisteredWebsite saved = websiteRepository.save(site);
        return WebsiteResponse.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public WebsiteResponse getWebsite(UUID id) {
        RegisteredWebsite site = websiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RegisteredWebsite", id));
        return WebsiteResponse.fromDomain(site);
    }

    @Transactional(readOnly = true)
    public List<WebsiteResponse> listAllWebsites() {
        return websiteRepository.findAll().stream()
                .map(WebsiteResponse::fromDomain)
                .toList();
    }
}
