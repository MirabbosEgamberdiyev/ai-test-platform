package com.platform.common.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainModelPrimitivesTest {

    @Test
    @DisplayName("Result monad handles success and failure branches correctly")
    void resultMonadBehavior() {
        Result<String, String> success = Result.success("OK");
        assertThat(success.isSuccess()).isTrue();
        assertThat(success.isFailure()).isFalse();
        assertThat(success.getValue()).contains("OK");
        assertThat(success.map(String::length).getValue()).contains(2);

        Result<String, String> failure = Result.failure("FAILED");
        assertThat(failure.isSuccess()).isFalse();
        assertThat(failure.isFailure()).isTrue();
        assertThat(failure.getError()).contains("FAILED");
        assertThatThrownBy(() -> failure.orElseThrow(IllegalStateException::new))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ConfidenceScore validates bounds and categorizes confidence level")
    void confidenceScoreValidation() {
        ConfidenceScore high = ConfidenceScore.of(0.92);
        assertThat(high.isHighConfidence()).isTrue();
        assertThat(high.toPercentage()).isEqualTo(92);

        ConfidenceScore medium = ConfidenceScore.of(0.75);
        assertThat(medium.isMediumConfidence()).isTrue();

        ConfidenceScore low = ConfidenceScore.of(0.45);
        assertThat(low.isLowConfidence()).isTrue();

        assertThatThrownBy(() -> new ConfidenceScore(new BigDecimal("1.05")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ConfidenceScore(new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TokenBudget accurately tracks consumption and availability")
    void tokenBudgetBehavior() {
        TokenBudget budget = new TokenBudget(10_000, 2_000);
        assertThat(budget.remainingTokens()).isEqualTo(8_000);
        assertThat(budget.hasAvailableTokens(5_000)).isTrue();
        assertThat(budget.hasAvailableTokens(8_001)).isFalse();

        TokenBudget updated = budget.withUsage(3_000);
        assertThat(updated.usedMonthlyTokens()).isEqualTo(5_000);
        assertThat(updated.remainingTokens()).isEqualTo(5_000);
    }

    @Test
    @DisplayName("TenantId and UserId strongly typed identity records")
    void strongIdentityRecords() {
        UUID rawId = UUID.randomUUID();
        TenantId tenantId = TenantId.of(rawId);
        UserId userId = UserId.of(rawId);

        assertThat(tenantId.value()).isEqualTo(rawId);
        assertThat(userId.value()).isEqualTo(rawId);
        assertThat(tenantId.toString()).isEqualTo(rawId.toString());
    }
}
