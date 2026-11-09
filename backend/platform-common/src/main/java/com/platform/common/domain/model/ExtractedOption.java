package com.platform.common.domain.model;

import java.io.Serializable;
import java.util.Objects;

public record ExtractedOption(
        String label, // e.g. "A", "B", "1"
        String text   // e.g. "Polymorphism enables..."
) implements Serializable {
    public ExtractedOption {
        Objects.requireNonNull(label, "Option label cannot be null");
        Objects.requireNonNull(text, "Option text cannot be null");
    }
}
