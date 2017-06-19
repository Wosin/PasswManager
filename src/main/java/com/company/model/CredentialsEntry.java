package com.company.model;

import com.company.crypto.CipheringService;

import javax.crypto.spec.IvParameterSpec;

public class CredentialsEntry {

    String credentialsCiphered;
    IvParameterSpec iv;
    String loggingPoint;

    public String getCredentialsCiphered() {
        return credentialsCiphered;
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    public String getLoggingPoint() {
        return loggingPoint;
    }

    public CredentialsEntry(IvParameterSpec iv, String loggingPoint, String cipheredCredentials) {
        this.iv = iv;
        this.loggingPoint = loggingPoint;
        this.credentialsCiphered = cipheredCredentials;
    }
}
