package com.cloudvault.core.config;

import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local-h2")
@Import(RabbitAutoConfiguration.class)
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "cloudvault-exchange";
    public static final String DLX_NAME = "cloudvault-dlx";
    
    public static final String VIRUS_SCAN_QUEUE = "virus-scanning-queue";
    public static final String STORAGE_ALERT_QUEUE = "storage-alert-queue";
    public static final String DLQ_QUEUE = "cloudvault-dlq";

    public static final String VIRUS_SCAN_ROUTING_KEY = "file.scan";
    public static final String STORAGE_ALERT_ROUTING_KEY = "workspace.quota";
    public static final String DLQ_ROUTING_KEY = "dead-letter";

    @Bean
    public TopicExchange cloudvaultExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue(DLQ_QUEUE, true);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue virusScanQueue() {
        return QueueBuilder.durable(VIRUS_SCAN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding virusScanBinding() {
        return BindingBuilder.bind(virusScanQueue())
                .to(cloudvaultExchange())
                .with(VIRUS_SCAN_ROUTING_KEY);
    }

    @Bean
    public Queue storageAlertQueue() {
        return QueueBuilder.durable(STORAGE_ALERT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding storageAlertBinding() {
        return BindingBuilder.bind(storageAlertQueue())
                .to(cloudvaultExchange())
                .with(STORAGE_ALERT_ROUTING_KEY);
    }
}
