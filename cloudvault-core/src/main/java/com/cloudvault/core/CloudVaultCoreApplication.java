package com.cloudvault.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
        RedisRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class
})
@ComponentScan(basePackages = "com.cloudvault")
@EnableConfigurationProperties
public class CloudVaultCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudVaultCoreApplication.class, args);
    }
}
