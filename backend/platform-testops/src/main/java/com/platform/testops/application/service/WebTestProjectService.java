package com.platform.testops.application.service;

import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.port.SsrfGuardPort;
import com.platform.testops.application.dto.CreateWebProjectCommand;
import com.platform.testops.application.dto.UpdateWebProjectCommand;
import com.platform.testops.application.dto.WebProjectResponse;
import com.platform.testops.application.port.WebTestProjectRepositoryPort;
import com.platform.testops.domain.model.WebTestProject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WebTestProjectService {

    private final WebTestProjectRepositoryPort projectRepository;
    private final SsrfGuardPort ssrfGuard;

    public WebTestProjectService(WebTestProjectRepositoryPort projectRepository, SsrfGuardPort ssrfGuard) {
        this.projectRepository = projectRepository;
        this.ssrfGuard = ssrfGuard;
    }

    public WebProjectResponse createProject(TenantId tenantId, CreateWebProjectCommand command) {
        ssrfGuard.validateUrl(command.targetUrl());

        WebTestProject project = WebTestProject.create(
                tenantId,
                null,
                command.name(),
                command.targetUrl(),
                command.environment() != null ? command.environment() : "PRODUCTION",
                command.description(),
                command.instructions(),
                command.isAuthorized() == null || command.isAuthorized()
        );

        WebTestProject saved = projectRepository.save(project);
        return WebProjectResponse.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public List<WebProjectResponse> listProjects(TenantId tenantId) {
        return projectRepository.findByTenantId(tenantId)
                .stream()
                .map(WebProjectResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public WebProjectResponse getProject(TenantId tenantId, UUID id) {
        WebTestProject project = projectRepository.findById(id)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + id));
        return WebProjectResponse.fromDomain(project);
    }

    public WebProjectResponse updateProject(TenantId tenantId, UUID id, UpdateWebProjectCommand command) {
        ssrfGuard.validateUrl(command.targetUrl());

        WebTestProject project = projectRepository.findById(id)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + id));

        project.update(
                command.name(),
                command.targetUrl(),
                command.environment(),
                command.description(),
                command.instructions(),
                command.isAuthorized() == null || command.isAuthorized()
        );

        return WebProjectResponse.fromDomain(projectRepository.save(project));
    }

    public void deleteProject(TenantId tenantId, UUID id) {
        WebTestProject project = projectRepository.findById(id)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + id));
        projectRepository.deleteById(project.getId());
    }
}
