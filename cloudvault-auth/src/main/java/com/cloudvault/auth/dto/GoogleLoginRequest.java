package com.cloudvault.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleLoginRequest {
    @NotBlank(message = "Google ID Token is required")
    private String idToken;

    private String deviceFingerprint;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }
}
