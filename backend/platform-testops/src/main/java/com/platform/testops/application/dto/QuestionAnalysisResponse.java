package com.platform.testops.application.dto;

import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.common.domain.model.ExtractedOption;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record QuestionAnalysisResponse(
        UUID questionId,
        UUID sessionId,
        int sequenceNumber,
        String questionText,
        List<ExtractedOption> options,
        String predictedAnswer,
        String reasoning,
        double confidenceScore,
        String providerName,
        String modelName
) implements Serializable {

    public static QuestionAnalysisResponse of(UUID questionId, UUID sessionId, int sequenceNumber,
                                              String questionText, List<ExtractedOption> options,
                                              AIAnalysisResult aiResult) {
        return new QuestionAnalysisResponse(
                questionId,
                sessionId,
                sequenceNumber,
                questionText,
                options,
                aiResult.predictedAnswer(),
                aiResult.reasoning(),
                aiResult.confidenceScore().value().doubleValue(),
                aiResult.providerName(),
                aiResult.modelName()
        );
    }
}
