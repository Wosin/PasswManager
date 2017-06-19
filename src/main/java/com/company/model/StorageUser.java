package com.company.model;

public class StorageUser {
  private String storageUsername;
  private String storagePassword;
  private String salt;
  private byte[] storageKey;

    public String getStoragePassword() {
        return storagePassword;
    }

    public String getSalt() {
        return salt;
    }

    public byte[] getStorageKey() {
        return storageKey;
    }

    public StorageUser(String storagePassword, UserEntry userEntry) {
        this.storagePassword = storagePassword;
        this.salt = userEntry.getSalt();
        this.storageKey = userEntry.getStorageKey();
        this.storageUsername = userEntry.getStorageUsername();
    }

    public String getStorageUsername() {
        return storageUsername;
    }
}
