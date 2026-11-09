package com.platform.common.infrastructure.security;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public record CurrentUser(
        UserId userId,
        TenantId tenantId,
        String email,
        Set<String> roles
) implements Serializable {

    public CurrentUser {
        roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
    }

    public boolean hasRole(String role) {
        return roles.contains(role) || roles.contains("ROLE_" + role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("ROLE_ADMIN");
    }
}
