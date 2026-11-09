package com.platform.testops.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.testops.application.dto.PracticeResultResponse;
import com.platform.testops.application.service.PracticeAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/results")
public class AnalyticsController {

    private final PracticeAnalyticsService analyticsService;

    public AnalyticsController(PracticeAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<PracticeResultResponse> getResultBySession(@PathVariable UUID sessionId) {
        PracticeResultResponse response = analyticsService.getResultForSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PracticeResultResponse>> listMyResults() {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        List<PracticeResultResponse> results = analyticsService.listResultsForUser(
                currentUser.tenantId(),
                currentUser.userId()
        );
        return ResponseEntity.ok(results);
    }
}
