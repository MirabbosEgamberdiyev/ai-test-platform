package com.platform.registry.presentation.rest;

import com.platform.registry.application.dto.RegisterWebsiteCommand;
import com.platform.registry.application.dto.WebsiteResponse;
import com.platform.registry.application.service.WebsiteRegistryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registry/websites")
public class WebsiteRegistryController {

    private final WebsiteRegistryService websiteRegistryService;

    public WebsiteRegistryController(WebsiteRegistryService websiteRegistryService) {
        this.websiteRegistryService = websiteRegistryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<WebsiteResponse> registerWebsite(@Valid @RequestBody RegisterWebsiteCommand command) {
        WebsiteResponse response = websiteRegistryService.registerWebsite(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WebsiteResponse>> listWebsites() {
        List<WebsiteResponse> response = websiteRegistryService.listAllWebsites();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebsiteResponse> getWebsite(@PathVariable UUID id) {
        WebsiteResponse response = websiteRegistryService.getWebsite(id);
        return ResponseEntity.ok(response);
    }
}
