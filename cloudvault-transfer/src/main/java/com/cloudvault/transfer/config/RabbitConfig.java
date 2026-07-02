package com.cloudvault.transfer.config;

import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local-h2")
@Import(RabbitAutoConfiguration.class)
public class RabbitConfig {
    // Exposes RabbitMQ auto-configuration for the transfer service in production
}
