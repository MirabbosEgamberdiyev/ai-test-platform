package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebTestCase;
import com.platform.testops.domain.model.WebTestCaseCategory;

import java.util.UUID;

public record WebTestCaseDto(
        UUID id,
        UUID planId,
        String title,
        WebTestCaseCategory category,
        String priority,
        String stepsJson,
        String expectedResult,
        int orderIndex
) {
    public static WebTestCaseDto fromDomain(WebTestCase domain) {
        return new WebTestCaseDto(
                domain.getId(),
                domain.getPlanId(),
                domain.getTitle(),
                domain.getCategory(),
                domain.getPriority(),
                domain.getStepsJson(),
                domain.getExpectedResult(),
                domain.getOrderIndex()
        );
    }
}
