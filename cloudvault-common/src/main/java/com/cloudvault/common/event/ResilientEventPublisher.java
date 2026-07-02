package com.cloudvault.common.event;

public interface ResilientEventPublisher {
    void publishEvent(String routingKey, Object payload);
}
