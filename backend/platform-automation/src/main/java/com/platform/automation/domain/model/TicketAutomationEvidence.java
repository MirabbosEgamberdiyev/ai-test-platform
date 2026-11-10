package com.platform.automation.domain.model;

public record TicketAutomationEvidence(
        String screenshotPath,
        String browserLogs,
        String evidenceJson,
        boolean success
) {}
