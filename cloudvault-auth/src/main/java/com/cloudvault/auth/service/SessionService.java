package com.cloudvault.auth.service;

import com.cloudvault.auth.config.JwtProperties;
import com.cloudvault.auth.model.User;
import com.cloudvault.auth.model.UserSession;
import com.cloudvault.auth.repository.UserSessionRepository;
import com.cloudvault.auth.security.JwtTokenProvider;
import com.cloudvault.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_SESSION_PREFIX = "session:token:";

    public SessionService(UserSessionRepository sessionRepository, JwtTokenProvider tokenProvider,
                          JwtProperties jwtProperties, RedisTemplate<String, Object> redisTemplate) {
        this.sessionRepository = sessionRepository;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public UserSession createSession(User user, String deviceFingerprint, String ipAddress, String userAgent) {
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(jwtProperties.getRefreshExpirationMs() * 1_000_000L);

        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .deviceFingerprint(deviceFingerprint)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .build();

        UserSession savedSession = sessionRepository.save(session);
        cacheSessionInRedis(savedSession);
        return savedSession;
    }

    @Transactional
    public UserSession rotateSession(String oldRefreshToken, String deviceFingerprint, String ipAddress, String userAgent) {
        UserSession session = sessionRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new UnauthorizedException("Session not found or invalid"));

        if (session.isExpired()) {
            sessionRepository.delete(session);
            evictSessionFromRedis(oldRefreshToken);
            throw new UnauthorizedException("Session has expired");
        }

        evictSessionFromRedis(oldRefreshToken);

        String newRefreshToken = tokenProvider.generateRefreshToken(session.getUser().getEmail());
        session.setRefreshToken(newRefreshToken);
        session.setDeviceFingerprint(deviceFingerprint);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plusNanos(jwtProperties.getRefreshExpirationMs() * 1_000_000L));

        UserSession updatedSession = sessionRepository.save(session);
        cacheSessionInRedis(updatedSession);
        return updatedSession;
    }

    @Transactional
    public void revokeSession(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
        evictSessionFromRedis(refreshToken);
    }

    @Transactional
    public void revokeAllSessionsForUser(UUID userId) {
        List<UserSession> sessions = sessionRepository.findByUserId(userId);
        sessionRepository.deleteByUserId(userId);
        for (UserSession s : sessions) {
            evictSessionFromRedis(s.getRefreshToken());
        }
    }

    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(UUID userId) {
        return sessionRepository.findByUserId(userId).stream()
                .filter(session -> !session.isExpired())
                .toList();
    }

    private void cacheSessionInRedis(UserSession session) {
        try {
            String key = REDIS_SESSION_PREFIX + session.getRefreshToken();
            long ttlSeconds = Duration.between(LocalDateTime.now(), session.getExpiresAt()).toSeconds();
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, session.getUser().getId().toString(), Duration.ofSeconds(ttlSeconds));
            }
        } catch (Exception e) {
            log.warn("Redis operations failed. Degrading to DB-only session tracking. Error: {}", e.getMessage());
        }
    }

    private void evictSessionFromRedis(String refreshToken) {
        try {
            String key = REDIS_SESSION_PREFIX + refreshToken;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis operations failed on eviction. Error: {}", e.getMessage());
        }
    }
}
