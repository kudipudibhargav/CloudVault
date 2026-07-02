package com.cloudvault.core.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class CollaborationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(CollaborationWebSocketHandler.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Local in-memory session registry mapping workspaceId -> active sessions
    private final Map<String, Set<WebSocketSession>> workspaceSessions = new ConcurrentHashMap<>();
    // Session to workspace mapping for fast lookups on close
    private final Map<String, String> sessionWorkspaceMap = new ConcurrentHashMap<>();

    public CollaborationWebSocketHandler(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            String type = jsonNode.get("type").asText();
            String workspaceId = jsonNode.get("workspaceId").asText();

            if ("SUBSCRIBE".equalsIgnoreCase(type)) {
                workspaceSessions.computeIfAbsent(workspaceId, k -> new CopyOnWriteArraySet<>()).add(session);
                sessionWorkspaceMap.put(session.getId(), workspaceId);
                log.info("Session {} subscribed to workspace {}", session.getId(), workspaceId);

                session.sendMessage(new TextMessage("{\"type\":\"ACK\",\"message\":\"Subscribed to " + workspaceId + "\"}"));

            } else if ("EDIT".equalsIgnoreCase(type)) {
                String payload = message.getPayload();
                log.info("Edit event received in workspace {}: {}", workspaceId, payload);

                // Publish to Redis Pub/Sub for horizontal scaling
                try {
                    redisTemplate.convertAndSend("workspace:collab:" + workspaceId, payload);
                } catch (Exception e) {
                    log.warn("Redis Pub/Sub offline, falling back to direct local broadcast: {}", e.getMessage());
                    broadcastLocally(workspaceId, payload);
                }
            }
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            session.sendMessage(new TextMessage("{\"type\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String workspaceId = sessionWorkspaceMap.remove(session.getId());
        if (workspaceId != null) {
            Set<WebSocketSession> sessions = workspaceSessions.get(workspaceId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    workspaceSessions.remove(workspaceId);
                }
            }
        }
        log.info("WebSocket connection closed: {}", session.getId());
    }

    /**
     * Called by Redis Message Listener when a message is received from Pub/Sub
     */
    public void handleRedisBroadcast(String channel, String message) {
        // Channel format: workspace:collab:{workspaceId}
        String prefix = "workspace:collab:";
        if (channel.startsWith(prefix)) {
            String workspaceId = channel.substring(prefix.length());
            broadcastLocally(workspaceId, message);
        }
    }

    public void broadcastLocally(String workspaceId, String message) {
        Set<WebSocketSession> sessions = workspaceSessions.get(workspaceId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.warn("Failed to send WebSocket message to session: {}", session.getId());
                }
            }
        }
    }
}
