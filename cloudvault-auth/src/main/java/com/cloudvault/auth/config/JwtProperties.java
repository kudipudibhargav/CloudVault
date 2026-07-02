package com.cloudvault.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudvault.jwt")
public class JwtProperties {
    private String secret = "dGVzdHNlY3JldGtleWZvcmNsb3VkdmF1bHRlbnRlcnByaXNlYXV0aHNlcnZpY2V3aXRoZW5vdWdoYml0czI1Ng=="; // Default secret for fallback
    private long expirationMs = 900000; // 15 minutes
    private long refreshExpirationMs = 604800000; // 7 days

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public void setRefreshExpirationMs(long refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }
}
