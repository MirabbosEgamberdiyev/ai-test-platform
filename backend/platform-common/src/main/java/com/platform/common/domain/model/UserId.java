package com.platform.common.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) implements Serializable {
    public UserId {
        Objects.requireNonNull(value, "User ID cannot be null");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String uuidStr) {
        return new UserId(UUID.fromString(uuidStr));
    }

    public static UserId of(UUID uuid) {
        return new UserId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
