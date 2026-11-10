package com.platform.testops.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.testops.application.dto.DiscoverUrlCommand;
import com.platform.testops.application.dto.SolveTicketsCommand;
import com.platform.testops.application.dto.UrlDiscoveryResponse;
import com.platform.testops.application.dto.UrlSolvingSessionResponse;
import com.platform.testops.application.service.UrlTestDiscoveryService;
import com.platform.testops.application.service.UrlTestSolvingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/url-tests")
public class UrlTestSolvingController {

    private final UrlTestDiscoveryService discoveryService;
    private final UrlTestSolvingService solvingService;

    public UrlTestSolvingController(UrlTestDiscoveryService discoveryService,
                                    UrlTestSolvingService solvingService) {
        this.discoveryService = discoveryService;
        this.solvingService = solvingService;
    }

    @PostMapping("/discover")
    public ResponseEntity<UrlDiscoveryResponse> discoverTickets(
            @Valid @RequestBody DiscoverUrlCommand command,
            @RequestParam(defaultValue = "uz-Latn") String language) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        UrlDiscoveryResponse response = discoveryService.discover(user.tenantId(), user.userId(), command, language);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/discovery/{id}")
    public ResponseEntity<UrlDiscoveryResponse> getDiscovery(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "uz-Latn") String language) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(discoveryService.getDiscoverySession(user.tenantId(), id, language));
    }

    @PostMapping("/solve")
    public ResponseEntity<UrlSolvingSessionResponse> solveTickets(
            @Valid @RequestBody SolveTicketsCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        UrlSolvingSessionResponse response = solvingService.solveTickets(user.tenantId(), user.userId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<UrlSolvingSessionResponse> getSession(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(solvingService.getSession(user.tenantId(), id));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<UrlSolvingSessionResponse>> listSessions() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(solvingService.listSessions(user.tenantId()));
    }

    @GetMapping("/sessions/{id}/export")
    public ResponseEntity<String> exportSession(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "json") String format) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        String content = solvingService.exportSession(user.tenantId(), id, format);

        MediaType mediaType = "csv".equalsIgnoreCase(format) ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_JSON;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"solved-tickets-" + id + "." + format + "\"")
                .body(content);
    }
}
