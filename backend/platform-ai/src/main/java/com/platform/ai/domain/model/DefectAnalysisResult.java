package com.platform.ai.domain.model;

public record DefectAnalysisResult(
        String rootCauseHypothesis,
        String recommendedFix,
        String defectClassification, // CONFIRMED_DEFECT, SUSPECTED_DEFECT, ENVIRONMENT_LIMITATION, TEST_CONFIG_ISSUE
        double confidenceScore
) {}
