package com.platform.common.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing a normalized AI confidence score between 0.00 and 1.00 (0% - 100%).
 */
public record ConfidenceScore(BigDecimal value) implements Serializable {

    public ConfidenceScore {
        Objects.requireNonNull(value, "Confidence score value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Confidence score must be strictly between 0.00 and 1.00; got " + value);
        }
    }

    public static ConfidenceScore of(double score) {
        return new ConfidenceScore(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
    }

    public static ConfidenceScore of(BigDecimal score) {
        return new ConfidenceScore(score.setScale(2, RoundingMode.HALF_UP));
    }

    public boolean isHighConfidence() {
        return value.compareTo(new BigDecimal("0.85")) >= 0;
    }

    public boolean isMediumConfidence() {
        return value.compareTo(new BigDecimal("0.60")) >= 0 && value.compareTo(new BigDecimal("0.85")) < 0;
    }

    public boolean isLowConfidence() {
        return value.compareTo(new BigDecimal("0.60")) < 0;
    }

    public int toPercentage() {
        return value.multiply(new BigDecimal("100")).intValue();
    }
}
