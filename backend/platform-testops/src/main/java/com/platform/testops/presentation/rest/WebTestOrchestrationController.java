package com.platform.testops.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.testops.application.dto.CreateWebTestPlanCommand;
import com.platform.testops.application.dto.StartWebTestRunCommand;
import com.platform.testops.application.dto.WebDashboardStatsResponse;
import com.platform.testops.application.dto.WebDefectResponse;
import com.platform.testops.application.dto.WebTestPlanResponse;
import com.platform.testops.application.dto.WebTestReportResponse;
import com.platform.testops.application.dto.WebTestRunResponse;
import com.platform.testops.application.service.WebTestOrchestrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WebTestOrchestrationController {

    private final WebTestOrchestrationService orchestrationService;

    public WebTestOrchestrationController(WebTestOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/web-tests/plans")
    public ResponseEntity<WebTestPlanResponse> generatePlan(@Valid @RequestBody CreateWebTestPlanCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        WebTestPlanResponse response = orchestrationService.generateTestPlan(user.tenantId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/web-tests/plans/{id}/approve")
    public ResponseEntity<WebTestPlanResponse> approvePlan(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.approveTestPlan(user.tenantId(), id));
    }

    @PostMapping("/web-tests/runs")
    public ResponseEntity<WebTestRunResponse> startTestRun(@Valid @RequestBody StartWebTestRunCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        WebTestRunResponse response = orchestrationService.startTestRun(user.tenantId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/web-tests/runs")
    public ResponseEntity<List<WebTestRunResponse>> listRuns() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.listTestRuns(user.tenantId()));
    }

    @GetMapping("/web-tests/runs/{id}")
    public ResponseEntity<WebTestRunResponse> getRun(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.getTestRun(user.tenantId(), id));
    }

    @GetMapping("/web-tests/runs/{id}/defects")
    public ResponseEntity<List<WebDefectResponse>> getDefects(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.getDefects(user.tenantId(), id));
    }

    @GetMapping("/web-tests/runs/{id}/report")
    public ResponseEntity<WebTestReportResponse> getReport(@PathVariable UUID id) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.getReport(user.tenantId(), id));
    }

    @GetMapping("/web-tests/runs/{id}/export")
    public ResponseEntity<String> exportReport(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "html") String format) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        String content = orchestrationService.exportReport(user.tenantId(), id, format);

        MediaType mediaType = "json".equalsIgnoreCase(format) ? MediaType.APPLICATION_JSON :
                ("csv".equalsIgnoreCase(format) ? MediaType.TEXT_PLAIN : MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + id + "." + format + "\"")
                .body(content);
    }

    @GetMapping("/web-dashboard/stats")
    public ResponseEntity<WebDashboardStatsResponse> getDashboardStats() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(orchestrationService.getDashboardStats(user.tenantId()));
    }
}
