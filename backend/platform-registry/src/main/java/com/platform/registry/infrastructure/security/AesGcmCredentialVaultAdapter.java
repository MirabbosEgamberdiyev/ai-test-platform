package com.platform.registry.infrastructure.security;

import com.platform.registry.application.port.CredentialVaultPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesGcmCredentialVaultAdapter implements CredentialVaultPort {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecretKey masterKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmCredentialVaultAdapter(
            @Value("${app.vault.master-key:c3VwZXItc2VjcmV0LW1hc3Rlci1rZXktMzItYnl0ZXM=}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.masterKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public EncryptedData encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, masterKey, spec);

            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            String ciphertextBase64 = Base64.getEncoder().encodeToString(cipherBytes);
            String ivBase64 = Base64.getEncoder().encodeToString(iv);

            return new EncryptedData(ciphertextBase64, ivBase64, 1);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt secret in vault", e);
        }
    }

    @Override
    public String decrypt(String ciphertextBase64, String ivBase64, int keyVersion) {
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(ciphertextBase64);
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, masterKey, spec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt secret from vault", e);
        }
    }
}
