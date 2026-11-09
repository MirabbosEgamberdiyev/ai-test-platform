package com.platform.registry.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.registry.application.dto.ConfigureIntegrationCommand;
import com.platform.registry.application.dto.IntegrationResponse;
import com.platform.registry.application.service.IntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/registry/integrations")
public class IntegrationController {

    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    public ResponseEntity<IntegrationResponse> configureIntegration(@Valid @RequestBody ConfigureIntegrationCommand command) {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        IntegrationResponse response = integrationService.configureIntegration(currentUser.tenantId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IntegrationResponse>> listIntegrations() {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        List<IntegrationResponse> response = integrationService.listTenantIntegrations(currentUser.tenantId());
        return ResponseEntity.ok(response);
    }
}
