package com.platform.common.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record SessionId(UUID value) implements Serializable {
    public SessionId {
        Objects.requireNonNull(value, "Session ID cannot be null");
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId of(String uuidStr) {
        return new SessionId(UUID.fromString(uuidStr));
    }

    public static SessionId of(UUID uuid) {
        return new SessionId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
