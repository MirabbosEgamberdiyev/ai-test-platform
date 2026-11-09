package com.platform.iam.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

public class User {
    private final UserId id;
    private final TenantId tenantId;
    private String email;
    private String passwordHash;
    private String fullName;
    private UserRole role;
    private UserStatus status;
    private String telegramChatId;
    private boolean mfaEnabled;
    private String mfaSecret;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UserId id, TenantId tenantId, String email, String passwordHash, String fullName,
                UserRole role, UserStatus status, String telegramChatId, boolean mfaEnabled,
                String mfaSecret, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.fullName = Objects.requireNonNull(fullName);
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
        this.telegramChatId = telegramChatId;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.version = version;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static User create(TenantId tenantId, String email, String passwordHash, String fullName, UserRole role) {
        Instant now = Instant.now();
        return new User(
                UserId.generate(),
                tenantId,
                email.toLowerCase().trim(),
                passwordHash,
                fullName.trim(),
                role != null ? role : UserRole.ROLE_LEARNER,
                UserStatus.ACTIVE,
                null,
                false,
                null,
                0L,
                now,
                now
        );
    }

    public UserId getId() {
        return id;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void linkTelegram(String chatId) {
        this.telegramChatId = Objects.requireNonNull(chatId);
        this.updatedAt = Instant.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash);
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
