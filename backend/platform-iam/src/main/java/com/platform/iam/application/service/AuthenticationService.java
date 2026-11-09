package com.platform.iam.application.service;

import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.exception.UnauthorizedException;
import com.platform.common.domain.model.UserId;
import com.platform.iam.application.dto.AuthTokenResponse;
import com.platform.iam.application.dto.AuthenticateUserCommand;
import com.platform.iam.application.dto.LinkTelegramCommand;
import com.platform.iam.application.dto.RefreshTokenCommand;
import com.platform.iam.application.dto.RegisterTenantCommand;
import com.platform.iam.application.dto.UserResponse;
import com.platform.iam.application.port.PasswordEncoderPort;
import com.platform.iam.application.port.RefreshTokenRepositoryPort;
import com.platform.iam.application.port.TenantRepositoryPort;
import com.platform.iam.application.port.TokenProviderPort;
import com.platform.iam.application.port.UserRepositoryPort;
import com.platform.iam.domain.model.RefreshToken;
import com.platform.iam.domain.model.Tenant;
import com.platform.iam.domain.model.TenantTier;
import com.platform.iam.domain.model.User;
import com.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationService {

    private final TenantRepositoryPort tenantRepository;
    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public AuthenticationService(TenantRepositoryPort tenantRepository,
                                 UserRepositoryPort userRepository,
                                 RefreshTokenRepositoryPort refreshTokenRepository,
                                 PasswordEncoderPort passwordEncoder,
                                 TokenProviderPort tokenProvider) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthTokenResponse registerTenant(RegisterTenantCommand cmd) {
        if (tenantRepository.existsBySlug(cmd.tenantSlug())) {
            throw new BusinessRuleViolationException("Tenant with slug '" + cmd.tenantSlug() + "' already exists");
        }
        if (userRepository.existsByEmail(cmd.adminEmail())) {
            throw new BusinessRuleViolationException("User with email '" + cmd.adminEmail() + "' already exists");
        }

        Tenant tenant = Tenant.create(cmd.tenantName(), cmd.tenantSlug(), TenantTier.FREE);
        Tenant savedTenant = tenantRepository.save(tenant);

        String encodedPassword = passwordEncoder.encode(cmd.adminPassword());
        User adminUser = User.create(
                savedTenant.getId(),
                cmd.adminEmail(),
                encodedPassword,
                cmd.adminFullName(),
                UserRole.ROLE_TENANT_ADMIN
        );
        User savedUser = userRepository.save(adminUser);

        return createAuthTokenResponse(savedUser);
    }

    public AuthTokenResponse authenticate(AuthenticateUserCommand cmd) {
        User user = userRepository.findByEmail(cmd.email().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(cmd.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is " + user.getStatus());
        }

        return createAuthTokenResponse(user);
    }

    public AuthTokenResponse refreshToken(RefreshTokenCommand cmd) {
        String rawToken = cmd.refreshToken();
        String hashedToken = tokenProvider.hashToken(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid or revoked refresh token"));

        if (!storedToken.isValid()) {
            refreshTokenRepository.revokeAllForUser(storedToken.getUserId());
            throw new UnauthorizedException("Refresh token is expired or compromised");
        }

        // Revoke the used refresh token (Token Rotation Principle)
        storedToken.revoke();
        refreshTokenRepository.save(storedToken);

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", storedToken.getUserId()));

        return createAuthTokenResponse(user);
    }

    public UserResponse linkTelegram(UserId userId, LinkTelegramCommand cmd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.linkTelegram(cmd.telegramChatId());
        User updated = userRepository.save(user);
        return UserResponse.fromDomain(updated);
    }

    private AuthTokenResponse createAuthTokenResponse(User user) {
        String accessToken = tokenProvider.generateAccessToken(user);
        String rawRefreshToken = tokenProvider.generateRefreshToken();
        String hashedRefreshToken = tokenProvider.hashToken(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), hashedRefreshToken, 7 * 24 * 3600L);
        refreshTokenRepository.save(refreshToken);

        return AuthTokenResponse.of(
                accessToken,
                rawRefreshToken,
                900L, // 15 minutes
                UserResponse.fromDomain(user)
        );
    }
}
