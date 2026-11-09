package com.platform.registry.application.service;

import com.platform.common.domain.exception.BusinessRuleViolationException;
import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.dto.ConfigureIntegrationCommand;
import com.platform.registry.application.dto.IntegrationResponse;
import com.platform.registry.application.port.CredentialVaultPort;
import com.platform.registry.application.port.IntegrationRepositoryPort;
import com.platform.registry.application.port.VaultSecretRepositoryPort;
import com.platform.registry.application.port.WebsiteRepositoryPort;
import com.platform.registry.domain.model.RegisteredWebsite;
import com.platform.registry.domain.model.TenantIntegration;
import com.platform.registry.domain.model.VaultSecret;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class IntegrationService {

    private final IntegrationRepositoryPort integrationRepository;
    private final WebsiteRepositoryPort websiteRepository;
    private final VaultSecretRepositoryPort vaultSecretRepository;
    private final CredentialVaultPort credentialVault;

    public IntegrationService(IntegrationRepositoryPort integrationRepository,
                              WebsiteRepositoryPort websiteRepository,
                              VaultSecretRepositoryPort vaultSecretRepository,
                              CredentialVaultPort credentialVault) {
        this.integrationRepository = integrationRepository;
        this.websiteRepository = websiteRepository;
        this.vaultSecretRepository = vaultSecretRepository;
        this.credentialVault = credentialVault;
    }

    public IntegrationResponse configureIntegration(TenantId tenantId, ConfigureIntegrationCommand cmd) {
        RegisteredWebsite website = websiteRepository.findById(cmd.websiteId())
                .orElseThrow(() -> new EntityNotFoundException("RegisteredWebsite", cmd.websiteId()));

        if (!website.isAllowed()) {
            throw new BusinessRuleViolationException("Cannot integrate with website: compliance status is " + website.getComplianceStatus());
        }

        TenantIntegration integration = integrationRepository.findByTenantIdAndWebsiteId(tenantId, website.getId())
                .orElseGet(() -> TenantIntegration.create(tenantId, website.getId(), cmd.authType(), cmd.configJson()));

        if (cmd.configJson() != null) {
            integration.updateConfig(cmd.configJson());
        }
        TenantSavedIntegration(integration, cmd.optionalSecretToVault());

        TenantIntegration saved = integrationRepository.save(integration);
        return IntegrationResponse.fromDomain(saved);
    }

    private void TenantSavedIntegration(TenantIntegration integration, String secret) {
        if (secret != null && !secret.isBlank()) {
            CredentialVaultPort.EncryptedData encrypted = credentialVault.encrypt(secret);
            VaultSecret vaultSecret = VaultSecret.create(
                    integration.getId(),
                    encrypted.ciphertextBase64(),
                    encrypted.ivBase64(),
                    encrypted.keyVersion(),
                    null
            );
            vaultSecretRepository.save(vaultSecret);
        }
    }

    @Transactional(readOnly = true)
    public List<IntegrationResponse> listTenantIntegrations(TenantId tenantId) {
        return integrationRepository.findByTenantId(tenantId).stream()
                .map(IntegrationResponse::fromDomain)
                .toList();
    }
}
