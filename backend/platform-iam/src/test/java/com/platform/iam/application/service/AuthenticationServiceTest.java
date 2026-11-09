package com.platform.iam.application.service;

import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.exception.UnauthorizedException;
import com.platform.iam.application.dto.AuthTokenResponse;
import com.platform.iam.application.dto.AuthenticateUserCommand;
import com.platform.iam.application.dto.RegisterTenantCommand;
import com.platform.iam.application.port.PasswordEncoderPort;
import com.platform.iam.application.port.RefreshTokenRepositoryPort;
import com.platform.iam.application.port.TenantRepositoryPort;
import com.platform.iam.application.port.TokenProviderPort;
import com.platform.iam.application.port.UserRepositoryPort;
import com.platform.iam.domain.model.Tenant;
import com.platform.iam.domain.model.User;
import com.platform.iam.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TenantRepositoryPort tenantRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private TokenProviderPort tokenProvider;

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(
                tenantRepository,
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                tokenProvider
        );
    }

    @Test
    @DisplayName("Successfully registers a new tenant with an admin user")
    void registerTenantSuccess() {
        RegisterTenantCommand cmd = new RegisterTenantCommand(
                "Acme Corp", "acme", "Alice Smith", "alice@acme.com", "SecretPass123!"
        );

        when(tenantRepository.existsBySlug("acme")).thenReturn(false);
        when(userRepository.existsByEmail("alice@acme.com")).thenReturn(false);
        when(passwordEncoder.encode("SecretPass123!")).thenReturn("hashed_pass");
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("mock_jwt");
        when(tokenProvider.generateRefreshToken()).thenReturn("mock_refresh");
        when(tokenProvider.hashToken(anyString())).thenReturn("hashed_refresh");

        AuthTokenResponse response = authService.registerTenant(cmd);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("mock_jwt");
        assertThat(response.refreshToken()).isEqualTo("mock_refresh");
        assertThat(response.user().email()).isEqualTo("alice@acme.com");
        assertThat(response.user().role()).isEqualTo(UserRole.ROLE_TENANT_ADMIN.name());

        verify(tenantRepository).save(any(Tenant.class));
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Fails registration when tenant slug already exists")
    void registerTenantDuplicateSlug() {
        RegisterTenantCommand cmd = new RegisterTenantCommand(
                "Acme Corp", "acme", "Alice", "alice@acme.com", "Password123!"
        );
        when(tenantRepository.existsBySlug("acme")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerTenant(cmd))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Authenticates user with correct credentials")
    void authenticateSuccess() {
        AuthenticateUserCommand cmd = new AuthenticateUserCommand("bob@example.com", "ValidPass");
        Tenant tenant = Tenant.create("Bob Org", "boborg", null);
        User user = User.create(tenant.getId(), "bob@example.com", "hashed_pass", "Bob", UserRole.ROLE_LEARNER);

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ValidPass", "hashed_pass")).thenReturn(true);
        when(tokenProvider.generateAccessToken(user)).thenReturn("jwt_token");
        when(tokenProvider.generateRefreshToken()).thenReturn("refresh_token");
        when(tokenProvider.hashToken(anyString())).thenReturn("hashed_ref");

        AuthTokenResponse response = authService.authenticate(cmd);

        assertThat(response.accessToken()).isEqualTo("jwt_token");
        assertThat(response.user().email()).isEqualTo("bob@example.com");
    }

    @Test
    @DisplayName("Rejects authentication with invalid password")
    void authenticateInvalidPassword() {
        AuthenticateUserCommand cmd = new AuthenticateUserCommand("bob@example.com", "WrongPass");
        Tenant tenant = Tenant.create("Bob Org", "boborg", null);
        User user = User.create(tenant.getId(), "bob@example.com", "hashed_pass", "Bob", UserRole.ROLE_LEARNER);

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "hashed_pass")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(cmd))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
