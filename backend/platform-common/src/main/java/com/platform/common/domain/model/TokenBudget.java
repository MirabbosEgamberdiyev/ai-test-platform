package com.platform.common.domain.model;

import java.io.Serializable;

public record TokenBudget(
        long maxMonthlyTokens,
        long usedMonthlyTokens
) implements Serializable {

    public TokenBudget {
        if (maxMonthlyTokens < 0 || usedMonthlyTokens < 0) {
            throw new IllegalArgumentException("Token counts cannot be negative");
        }
    }

    public boolean hasAvailableTokens(long requestedTokens) {
        return (usedMonthlyTokens + requestedTokens) <= maxMonthlyTokens;
    }

    public long remainingTokens() {
        return Math.max(0, maxMonthlyTokens - usedMonthlyTokens);
    }

    public TokenBudget withUsage(long addedTokens) {
        return new TokenBudget(this.maxMonthlyTokens, this.usedMonthlyTokens + addedTokens);
    }
}
