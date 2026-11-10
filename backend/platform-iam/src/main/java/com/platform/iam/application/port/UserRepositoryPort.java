package com.platform.iam.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.iam.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
    Optional<User> findByTenantIdAndEmail(TenantId tenantId, String email);
    boolean existsByEmail(String email);
    Optional<User> findByTelegramChatId(String chatId);
    Optional<User> findByGoogleId(String googleId);
}
