package com.platform.iam.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.iam.application.dto.AuthTokenResponse;
import com.platform.iam.application.dto.AuthenticateUserCommand;
import com.platform.iam.application.dto.ChangePasswordCommand;
import com.platform.iam.application.dto.ForgotPasswordCommand;
import com.platform.iam.application.dto.GoogleAuthCommand;
import com.platform.iam.application.dto.LinkTelegramCommand;
import com.platform.iam.application.dto.RefreshTokenCommand;
import com.platform.iam.application.dto.RegisterTenantCommand;
import com.platform.iam.application.dto.ResetPasswordCommand;
import com.platform.iam.application.dto.UserResponse;
import com.platform.iam.application.dto.VerifyEmailCommand;
import com.platform.iam.application.service.AuthenticationService;
import com.platform.iam.application.service.GoogleOAuthService;
import com.platform.iam.application.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final GoogleOAuthService googleOAuthService;
    private final PasswordRecoveryService passwordRecoveryService;

    public AuthController(AuthenticationService authService,
                          GoogleOAuthService googleOAuthService,
                          PasswordRecoveryService passwordRecoveryService) {
        this.authService = authService;
        this.googleOAuthService = googleOAuthService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse> register(@Valid @RequestBody RegisterTenantCommand command) {
        AuthTokenResponse response = authService.registerTenant(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody AuthenticateUserCommand command) {
        AuthTokenResponse response = authService.authenticate(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<AuthTokenResponse> googleAuth(@Valid @RequestBody GoogleAuthCommand command) {
        AuthTokenResponse response = googleOAuthService.authenticateGoogle(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordCommand command) {
        String token = passwordRecoveryService.requestPasswordReset(command);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset instructions have been dispatched",
                "status", "SUCCESS",
                "resetToken", token
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordCommand command) {
        passwordRecoveryService.resetPassword(command);
        return ResponseEntity.ok(Map.of(
                "message", "Password has been successfully reset",
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        passwordRecoveryService.changePassword(user.userId(), command);
        return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully",
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody VerifyEmailCommand command) {
        passwordRecoveryService.verifyEmail(command);
        return ResponseEntity.ok(Map.of(
                "message", "Email verified successfully",
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAll() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        passwordRecoveryService.logoutAllDevices(user.userId());
        return ResponseEntity.ok(Map.of(
                "message", "All active sessions revoked across all devices",
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenCommand command) {
        AuthTokenResponse response = authService.refreshToken(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUser> me() {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/link-telegram")
    public ResponseEntity<UserResponse> linkTelegram(@Valid @RequestBody LinkTelegramCommand command) {
        CurrentUser user = SecurityUtils.getRequiredCurrentUser();
        UserResponse response = authService.linkTelegram(user.userId(), command);
        return ResponseEntity.ok(response);
    }
}
