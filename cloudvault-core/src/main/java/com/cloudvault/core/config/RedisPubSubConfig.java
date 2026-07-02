package com.cloudvault.core.config;

import com.cloudvault.core.websocket.CollaborationWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@Profile("!local-h2")
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("workspace:collab:*"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(CollaborationWebSocketHandler receiver) {
        // Directs incoming Redis pub/sub messages to handleRedisBroadcast(String, String) in handler
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "handleRedisBroadcast");
        // Ensure serialization passes channels and messages as strings
        return adapter;
    }
}
