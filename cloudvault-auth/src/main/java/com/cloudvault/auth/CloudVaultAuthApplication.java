package com.cloudvault.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class })
@ComponentScan(basePackages = "com.cloudvault")
@EnableConfigurationProperties
public class CloudVaultAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudVaultAuthApplication.class, args);
    }
}
