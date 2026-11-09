package com.platform.testops.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.testops.application.dto.PracticeResultResponse;
import com.platform.testops.application.dto.PracticeSessionResponse;
import com.platform.testops.application.dto.QuestionAnalysisResponse;
import com.platform.testops.application.dto.StartPracticeCommand;
import com.platform.testops.application.dto.SubmitPracticeAnswerCommand;
import com.platform.testops.application.service.PracticeOrchestrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/practice/sessions")
public class PracticeSessionController {

    private final PracticeOrchestrationService orchestrationService;

    public PracticeSessionController(PracticeOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping
    public ResponseEntity<PracticeSessionResponse> startSession(@Valid @RequestBody StartPracticeCommand command) {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        PracticeSessionResponse response = orchestrationService.startSession(
                currentUser.tenantId(),
                currentUser.userId(),
                command
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{sessionId}/analyze")
    public ResponseEntity<QuestionAnalysisResponse> analyzeQuestion(@PathVariable UUID sessionId) {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        QuestionAnalysisResponse response = orchestrationService.analyzeCurrentQuestion(
                currentUser.tenantId(),
                currentUser.userId(),
                sessionId
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<PracticeSessionResponse> submitAnswer(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitPracticeAnswerCommand command) {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        PracticeSessionResponse response = orchestrationService.submitAnswer(
                currentUser.tenantId(),
                currentUser.userId(),
                command
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<PracticeResultResponse> finishSession(@PathVariable UUID sessionId) {
        CurrentUser currentUser = SecurityUtils.getRequiredCurrentUser();
        PracticeResultResponse response = orchestrationService.finishSession(
                currentUser.tenantId(),
                currentUser.userId(),
                sessionId
        );
        return ResponseEntity.ok(response);
    }
}
