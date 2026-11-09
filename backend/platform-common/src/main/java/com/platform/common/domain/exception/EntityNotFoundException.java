package com.platform.common.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s with identifier '%s' was not found", entityName, id), "RESOURCE_NOT_FOUND");
    }

    public EntityNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}
