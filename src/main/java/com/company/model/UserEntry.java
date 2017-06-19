package com.company.model;

public class UserEntry {
    private String storageUsername;
    private String storagePasswordHashed;
    private String salt;
    private byte[] storageKey;

    public UserEntry(String storageUsername, String storagePasswordHashed, String salt, byte[] storageKey) {
        this.storageUsername = storageUsername;
        this.storagePasswordHashed = storagePasswordHashed;
        this.salt = salt;
        this.storageKey = storageKey;
    }

    public String getStorageUsername() {

        return storageUsername;
    }

    public String getStoragePasswordHashed() {
        return storagePasswordHashed;
    }

    public String getSalt() {
        return salt;
    }

    public byte[] getStorageKey() {
        return storageKey;
    }
}

