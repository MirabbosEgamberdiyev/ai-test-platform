package com.platform.common.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record TenantId(UUID value) implements Serializable {
    public TenantId {
        Objects.requireNonNull(value, "Tenant ID cannot be null");
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    public static TenantId of(String uuidStr) {
        return new TenantId(UUID.fromString(uuidStr));
    }

    public static TenantId of(UUID uuid) {
        return new TenantId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
