package com.platform.automation.domain.model;

import com.platform.common.domain.model.ExtractedOption;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record ExtractedQuestionDom(
        String questionText,
        List<ExtractedOption> options,
        String questionType,
        String imageBase64,
        String rawHtml
) implements Serializable {

    public ExtractedQuestionDom {
        Objects.requireNonNull(questionText, "Question text cannot be null");
        options = options != null ? List.copyOf(options) : Collections.emptyList();
    }
}
