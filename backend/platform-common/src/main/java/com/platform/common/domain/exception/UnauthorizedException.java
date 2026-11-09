package com.platform.common.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED_ACCESS");
    }
}
