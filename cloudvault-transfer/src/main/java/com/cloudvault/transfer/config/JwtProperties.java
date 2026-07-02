package com.cloudvault.transfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudvault.jwt")
public class JwtProperties {
    private String secret = "dGVzdHNlY3JldGtleWZvcmNsb3VkdmF1bHRlbnRlcnByaXNlYXV0aHNlcnZpY2V3aXRoZW5vdWdoYml0czI1Ng==";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
