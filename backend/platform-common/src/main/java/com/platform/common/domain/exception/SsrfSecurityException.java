package com.platform.common.domain.exception;

public class SsrfSecurityException extends DomainException {
    public SsrfSecurityException(String message) {
        super(message, "SSRF_SECURITY_VIOLATION");
    }
}
