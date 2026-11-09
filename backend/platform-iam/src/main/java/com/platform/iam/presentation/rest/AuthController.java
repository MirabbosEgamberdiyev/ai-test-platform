package com.platform.iam.presentation.rest;

import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.common.infrastructure.security.SecurityUtils;
import com.platform.iam.application.dto.AuthTokenResponse;
import com.platform.iam.application.dto.AuthenticateUserCommand;
import com.platform.iam.application.dto.LinkTelegramCommand;
import com.platform.iam.application.dto.RefreshTokenCommand;
import com.platform.iam.application.dto.RegisterTenantCommand;
import com.platform.iam.application.dto.UserResponse;
import com.platform.iam.application.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
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
