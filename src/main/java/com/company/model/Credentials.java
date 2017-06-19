package com.company.model;

public class Credentials {
    private String username;

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Credentials(String[] credentialsArray) {
        this.username = credentialsArray[0];
        this.password = credentialsArray[1];
    }
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
