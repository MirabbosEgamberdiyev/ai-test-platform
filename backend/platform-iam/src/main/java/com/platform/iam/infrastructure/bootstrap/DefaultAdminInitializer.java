package com.platform.iam.infrastructure.bootstrap;

import com.platform.iam.application.port.PasswordEncoderPort;
import com.platform.iam.application.port.TenantRepositoryPort;
import com.platform.iam.application.port.UserRepositoryPort;
import com.platform.iam.domain.model.Tenant;
import com.platform.iam.domain.model.TenantTier;
import com.platform.iam.domain.model.User;
import com.platform.iam.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private final UserRepositoryPort userRepository;
    private final TenantRepositoryPort tenantRepository;
    private final PasswordEncoderPort passwordEncoder;

    public DefaultAdminInitializer(UserRepositoryPort userRepository,
                                   TenantRepositoryPort tenantRepository,
                                   PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            String encodedPassword = passwordEncoder.encode("Admin.123$");

            // 1. Ensure default demo tenant exists
            Tenant defaultTenant = tenantRepository.findBySlug("tenant-demo-001")
                    .orElseGet(() -> {
                        Tenant t = Tenant.create("Enterprise Admin Workspace", "tenant-demo-001", TenantTier.ENTERPRISE);
                        return tenantRepository.save(t);
                    });

            // 2. Ensure "admin@enterprise-demo.com" has password "Admin.123$"
            Optional<User> demoUserOpt = userRepository.findByEmail("admin@enterprise-demo.com");
            if (demoUserOpt.isPresent()) {
                User demoUser = demoUserOpt.get();
                demoUser.changePassword(encodedPassword);
                userRepository.save(demoUser);
                log.info("Default user 'admin@enterprise-demo.com' password set to Admin.123$");
            } else {
                User demoUser = User.create(
                        defaultTenant.getId(),
                        "admin@enterprise-demo.com",
                        encodedPassword,
                        "Admin",
                        UserRole.ROLE_TENANT_ADMIN
                );
                userRepository.save(demoUser);
                log.info("Default user 'admin@enterprise-demo.com' created with password Admin.123$");
            }

            // 3. Ensure "admin@aitestops.io" exists with password "Admin.123$"
            Optional<User> adminOpt = userRepository.findByEmail("admin@aitestops.io");
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                admin.changePassword(encodedPassword);
                userRepository.save(admin);
            } else {
                User admin = User.create(
                        defaultTenant.getId(),
                        "admin@aitestops.io",
                        encodedPassword,
                        "Admin",
                        UserRole.ROLE_TENANT_ADMIN
                );
                userRepository.save(admin);
                log.info("Default user 'admin@aitestops.io' created with password Admin.123$");
            }
        } catch (Exception e) {
            log.warn("Could not initialize default admin credentials on startup: {}", e.getMessage());
        }
    }
}
