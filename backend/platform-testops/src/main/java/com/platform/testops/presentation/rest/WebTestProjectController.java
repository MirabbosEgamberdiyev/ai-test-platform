package com.platform.testops.presentation.rest;

import com.platform.automation.domain.model.WebsiteDiscoveryResult;
import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.testops.application.dto.CreateWebProjectCommand;
import com.platform.testops.application.dto.UpdateWebProjectCommand;
import com.platform.testops.application.dto.WebProjectResponse;
import com.platform.testops.application.service.WebTestOrchestrationService;
import com.platform.testops.application.service.WebTestProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/web-projects")
public class WebTestProjectController {

    private final WebTestProjectService projectService;
    private final WebTestOrchestrationService orchestrationService;

    public WebTestProjectController(WebTestProjectService projectService, WebTestOrchestrationService orchestrationService) {
        this.projectService = projectService;
        this.orchestrationService = orchestrationService;
    }

    @PostMapping
    public ResponseEntity<WebProjectResponse> createProject(@Valid @RequestBody CreateWebProjectCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        WebProjectResponse response = projectService.createProject(user.tenantId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WebProjectResponse>> listProjects() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(projectService.listProjects(user.tenantId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebProjectResponse> getProject(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(projectService.getProject(user.tenantId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWebProjectCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(projectService.updateProject(user.tenantId(), id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        projectService.deleteProject(user.tenantId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/discover")
    public ResponseEntity<WebsiteDiscoveryResult> discoverWebsite(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.discoverWebsite(user.tenantId(), id));
    }
}
