package com.platform.ai.domain.model;

import com.platform.common.domain.model.ExtractedOption;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record AIAnalysisRequest(
        String questionPrompt,
        AIQuestionType questionType,
        List<ExtractedOption> options,
        String base64ImageData,
        String contextHint
) implements Serializable {

    public AIAnalysisRequest {
        Objects.requireNonNull(questionPrompt, "Question prompt cannot be null");
        options = options != null ? List.copyOf(options) : Collections.emptyList();
        questionType = questionType != null ? questionType : AIQuestionType.MULTIPLE_CHOICE;
    }

    public static AIAnalysisRequest ofText(String prompt, List<ExtractedOption> options) {
        return new AIAnalysisRequest(prompt, AIQuestionType.MULTIPLE_CHOICE, options, null, null);
    }

    public static AIAnalysisRequest ofMultimodal(String prompt, List<ExtractedOption> options, String base64ImageData) {
        return new AIAnalysisRequest(prompt, AIQuestionType.MULTIPLE_CHOICE, options, base64ImageData, null);
    }
}
