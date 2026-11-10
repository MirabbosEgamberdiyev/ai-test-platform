package com.platform.iam.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.iam.domain.model.User;
import com.platform.iam.domain.model.UserRole;
import com.platform.iam.domain.model.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "iam_users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status;

    @Column(name = "telegram_chat_id", length = 50)
    private String telegramChatId;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Column(name = "mfa_secret", length = 100)
    private String mfaSecret;

    @Column(name = "google_id", length = 100)
    private String googleId;

    @Column(name = "auth_provider", nullable = false, length = 30)
    private String authProvider;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserJpaEntity() {}

    public static UserJpaEntity fromDomain(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId().value();
        entity.tenantId = user.getTenantId().value();
        entity.email = user.getEmail();
        entity.passwordHash = user.getPasswordHash();
        entity.fullName = user.getFullName();
        entity.role = user.getRole();
        entity.status = user.getStatus();
        entity.telegramChatId = user.getTelegramChatId();
        entity.mfaEnabled = user.isMfaEnabled();
        entity.mfaSecret = user.getMfaSecret();
        entity.googleId = user.getGoogleId();
        entity.authProvider = user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL";
        entity.emailVerified = user.isEmailVerified();
        entity.version = user.getVersion();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    public User toDomain() {
        return new User(
                UserId.of(this.id),
                TenantId.of(this.tenantId),
                this.email,
                this.passwordHash,
                this.fullName,
                this.role,
                this.status,
                this.telegramChatId,
                this.mfaEnabled,
                this.mfaSecret,
                this.googleId,
                this.authProvider != null ? this.authProvider : "LOCAL",
                this.emailVerified,
                this.version != null ? this.version : 0L,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public String getTelegramChatId() { return telegramChatId; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public String getMfaSecret() { return mfaSecret; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
