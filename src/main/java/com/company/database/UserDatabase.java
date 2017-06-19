package com.company.database;

import com.company.model.StorageUser;

import java.util.Optional;

public interface UserDatabase {

    boolean registerUser(String username, String password) throws Exception;
    Optional<StorageUser> getUser(String username, String password) throws Exception;
}
