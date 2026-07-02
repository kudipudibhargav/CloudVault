package com.cloudvault.auth.dto;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private boolean twoFaRequired;

    public TokenResponse() {
    }

    public TokenResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, boolean twoFaRequired) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.twoFaRequired = twoFaRequired;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isTwoFaRequired() {
        return twoFaRequired;
    }

    public void setTwoFaRequired(boolean twoFaRequired) {
        this.twoFaRequired = twoFaRequired;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private boolean twoFaRequired;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder twoFaRequired(boolean twoFaRequired) {
            this.twoFaRequired = twoFaRequired;
            return this;
        }

        public TokenResponse build() {
            return new TokenResponse(accessToken, refreshToken, tokenType, expiresIn, twoFaRequired);
        }
    }
}
