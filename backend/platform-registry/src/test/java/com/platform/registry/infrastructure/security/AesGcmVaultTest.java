package com.platform.registry.infrastructure.security;

import com.platform.registry.application.port.CredentialVaultPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AesGcmVaultTest {

    @Test
    @DisplayName("Encrypts and decrypts secret string with AES-256-GCM authenticated cipher")
    void encryptAndDecryptRoundTrip() {
        // 32-byte AES key in base64
        byte[] rawKey = new byte[32];
        for (int i = 0; i < 32; i++) rawKey[i] = (byte) (i + 1);
        String base64Key = Base64.getEncoder().encodeToString(rawKey);

        AesGcmCredentialVaultAdapter vault = new AesGcmCredentialVaultAdapter(base64Key);

        String originalSecret = "SuperSensitiveApiToken_12345!@#$";
        CredentialVaultPort.EncryptedData encrypted = vault.encrypt(originalSecret);

        assertThat(encrypted.ciphertextBase64()).isNotBlank().isNotEqualTo(originalSecret);
        assertThat(encrypted.ivBase64()).isNotBlank();
        assertThat(encrypted.keyVersion()).isEqualTo(1);

        String decrypted = vault.decrypt(encrypted.ciphertextBase64(), encrypted.ivBase64(), encrypted.keyVersion());
        assertThat(decrypted).isEqualTo(originalSecret);
    }
}
