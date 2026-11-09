package com.platform.registry.application.port;

public interface CredentialVaultPort {
    record EncryptedData(String ciphertextBase64, String ivBase64, int keyVersion) {}

    EncryptedData encrypt(String plaintext);
    String decrypt(String ciphertextBase64, String ivBase64, int keyVersion);
}
