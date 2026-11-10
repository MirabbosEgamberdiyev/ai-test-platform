package com.platform.iam.application.service;

import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.exception.UnauthorizedException;
import com.platform.common.domain.model.UserId;
import com.platform.iam.application.dto.ChangePasswordCommand;
import com.platform.iam.application.dto.ForgotPasswordCommand;
import com.platform.iam.application.dto.ResetPasswordCommand;
import com.platform.iam.application.dto.VerifyEmailCommand;
import com.platform.iam.application.port.PasswordEncoderPort;
import com.platform.iam.application.port.RefreshTokenRepositoryPort;
import com.platform.iam.application.port.UserRepositoryPort;
import com.platform.iam.domain.model.User;
import com.platform.iam.infrastructure.persistence.SpringDataUserSecurityTokenRepository;
import com.platform.iam.infrastructure.persistence.UserSecurityTokenJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordRecoveryService {

    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryService.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final SpringDataUserSecurityTokenRepository tokenRepository;

    public PasswordRecoveryService(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   RefreshTokenRepositoryPort refreshTokenRepository,
                                   SpringDataUserSecurityTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenRepository = tokenRepository;
    }

    public String requestPasswordReset(ForgotPasswordCommand cmd) {
        String email = cmd.email().toLowerCase().trim();
        log.info("Password reset requested for email: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // OWASP Account Enumeration Prevention: do not reveal that email does not exist
            log.info("Password reset requested for non-existent email: {}. Returning silently.", email);
            return "RESET_TOKEN_DISPATCHED";
        }

        User user = userOpt.get();
        String rawToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String hashedToken = hashToken(rawToken);

        // Invalidate old reset tokens for this user
        tokenRepository.deleteByUserId(user.getId().value());

        // Save new 15-minute expiration token
        UserSecurityTokenJpaEntity tokenEntity = new UserSecurityTokenJpaEntity(
                UUID.randomUUID(),
                user.getId().value(),
                hashedToken,
                "PASSWORD_RESET",
                Instant.now().plus(Duration.ofMinutes(15))
        );
        tokenRepository.save(tokenEntity);

        log.info("[EMAIL NOTIFICATION] Password reset link generated for {}: /reset-password?token={}",
                email, rawToken);

        return rawToken;
    }

    public void resetPassword(ResetPasswordCommand cmd) {
        String hashed = hashToken(cmd.token().trim());
        UserSecurityTokenJpaEntity token = tokenRepository.findByTokenHashAndTokenType(hashed, "PASSWORD_RESET")
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired password reset token"));

        if (!token.isValid()) {
            throw new UnauthorizedException("Password reset token is expired or has already been used");
        }

        User user = userRepository.findById(UserId.of(token.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found for token"));

        String newPasswordHash = passwordEncoder.encode(cmd.newPassword());
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        // Mark token used
        token.markUsed();
        tokenRepository.save(token);

        // Revoke all active sessions across all devices for security
        refreshTokenRepository.revokeAllForUser(user.getId());
        log.info("Password successfully reset and all sessions revoked for user: {}", user.getEmail());
    }

    public void changePassword(UserId userId, ChangePasswordCommand cmd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (!passwordEncoder.matches(cmd.currentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        String newPasswordHash = passwordEncoder.encode(cmd.newPassword());
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        log.info("Password successfully updated for user: {}", user.getEmail());
    }

    public void verifyEmail(VerifyEmailCommand cmd) {
        String hashed = hashToken(cmd.token().trim());
        UserSecurityTokenJpaEntity token = tokenRepository.findByTokenHashAndTokenType(hashed, "EMAIL_VERIFICATION")
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired email verification token"));

        if (!token.isValid()) {
            throw new UnauthorizedException("Email verification token is expired or has already been used");
        }

        User user = userRepository.findById(UserId.of(token.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found for token"));

        user.markEmailVerified();
        userRepository.save(user);

        token.markUsed();
        tokenRepository.save(token);

        log.info("Email successfully verified for user: {}", user.getEmail());
    }

    public void logoutAllDevices(UserId userId) {
        refreshTokenRepository.revokeAllForUser(userId);
        log.info("Revoked all active refresh tokens for user: {}", userId.value());
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Cryptographic SHA-256 failure", e);
        }
    }
}
