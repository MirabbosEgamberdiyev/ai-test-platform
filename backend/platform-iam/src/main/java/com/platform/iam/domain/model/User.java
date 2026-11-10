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
    private String googleId;
    private String authProvider;
    private boolean emailVerified;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UserId id, TenantId tenantId, String email, String passwordHash, String fullName,
                UserRole role, UserStatus status, String telegramChatId, boolean mfaEnabled,
                String mfaSecret, String googleId, String authProvider, boolean emailVerified,
                long version, Instant createdAt, Instant updatedAt) {
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
        this.googleId = googleId;
        this.authProvider = authProvider != null ? authProvider : "LOCAL";
        this.emailVerified = emailVerified;
        this.version = version;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public User(UserId id, TenantId tenantId, String email, String passwordHash, String fullName,
                UserRole role, UserStatus status, String telegramChatId, boolean mfaEnabled,
                String mfaSecret, long version, Instant createdAt, Instant updatedAt) {
        this(id, tenantId, email, passwordHash, fullName, role, status, telegramChatId, mfaEnabled,
             mfaSecret, null, "LOCAL", false, version, createdAt, updatedAt);
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
                null,
                "LOCAL",
                false,
                0L,
                now,
                now
        );
    }

    public static User createWithGoogle(TenantId tenantId, String email, String fullName, String googleSub) {
        Instant now = Instant.now();
        return new User(
                UserId.generate(),
                tenantId,
                email.toLowerCase().trim(),
                "OAUTH2_GOOGLE_NO_PASSWORD",
                fullName != null && !fullName.isBlank() ? fullName.trim() : email.split("@")[0],
                UserRole.ROLE_LEARNER,
                UserStatus.ACTIVE,
                null,
                false,
                null,
                googleSub,
                "GOOGLE",
                true,
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

    public String getGoogleId() {
        return googleId;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void linkGoogle(String googleSub) {
        this.googleId = Objects.requireNonNull(googleSub);
        if ("LOCAL".equalsIgnoreCase(this.authProvider)) {
            this.authProvider = "GOOGLE";
        }
        this.emailVerified = true;
        this.updatedAt = Instant.now();
    }

    public void markEmailVerified() {
        this.emailVerified = true;
        this.updatedAt = Instant.now();
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
