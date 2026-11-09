package com.platform.iam.infrastructure.security;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.common.infrastructure.security.CurrentUser;
import com.platform.iam.infrastructure.adapter.JwtTokenProviderAdapter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderAdapter tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProviderAdapter tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenProvider.validateAccessToken(token)) {
                Claims claims = tokenProvider.extractClaims(token);

                String email = claims.getSubject();
                String userIdStr = claims.get("userId", String.class);
                String tenantIdStr = claims.get("tenantId", String.class);
                String roleStr = claims.get("role", String.class);

                CurrentUser currentUser = new CurrentUser(
                        UserId.of(userIdStr),
                        TenantId.of(tenantIdStr),
                        email,
                        Set.of(roleStr)
                );

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        List.of(new SimpleGrantedAuthority(roleStr))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
