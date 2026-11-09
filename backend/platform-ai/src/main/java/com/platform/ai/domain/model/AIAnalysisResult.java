package com.platform.ai.domain.model;

import com.platform.common.domain.model.ConfidenceScore;

import java.io.Serializable;
import java.util.Objects;

public record AIAnalysisResult(
        String predictedAnswer,
        String reasoning,
        ConfidenceScore confidenceScore,
        int promptTokens,
        int completionTokens,
        long latencyMs,
        String providerName,
        String modelName
) implements Serializable {

    public AIAnalysisResult {
        Objects.requireNonNull(predictedAnswer, "Predicted answer cannot be null");
        Objects.requireNonNull(reasoning, "Reasoning explanation cannot be null");
        Objects.requireNonNull(confidenceScore, "Confidence score cannot be null");
    }

    public int totalTokens() {
        return promptTokens + completionTokens;
    }
}
