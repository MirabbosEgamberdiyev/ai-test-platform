package com.platform.common.domain.exception;

public class QuotaExceededException extends DomainException {
    public QuotaExceededException(String message) {
        super(message, "QUOTA_EXCEEDED");
    }
}
