package com.example.kafka.model;

public class AuthResponse {
    private String authorizationStatus;

    public AuthResponse(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }
}
