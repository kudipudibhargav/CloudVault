package com.cloudvault.core.service;

import com.cloudvault.common.event.ResilientEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!local-h2")
public class RabbitEventPublisher implements ResilientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishEvent(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend("cloudvault-exchange", routingKey, payload);
    }
}
