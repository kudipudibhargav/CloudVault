package com.cloudvault.core.service;

import com.cloudvault.common.event.ResilientEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("local-h2")
public class LocalEventPublisher implements ResilientEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LocalEventPublisher.class);

    private final List<PublishedEvent> publishedEvents = new ArrayList<>();

    @Override
    public void publishEvent(String routingKey, Object payload) {
        log.info("LOCAL TEST AMQP BROADCAST: RoutingKey: {}, Payload: {}", routingKey, payload);
        publishedEvents.add(new PublishedEvent(routingKey, payload));
    }

    public List<PublishedEvent> getPublishedEvents() {
        return new ArrayList<>(publishedEvents);
    }

    public void clear() {
        publishedEvents.clear();
    }

    public static class PublishedEvent {
        private final String routingKey;
        private final Object payload;

        public PublishedEvent(String routingKey, Object payload) {
            this.routingKey = routingKey;
            this.payload = payload;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public Object getPayload() {
            return payload;
        }
    }
}
