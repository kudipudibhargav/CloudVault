package com.cloudvault.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Rate limit by Authorization token if present, fallback to client IP
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return Mono.just(authHeader.substring(7));
            }
            return Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .defaultIfEmpty("anonymous");
        };
    }
}
