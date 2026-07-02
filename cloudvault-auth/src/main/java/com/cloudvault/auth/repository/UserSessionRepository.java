package com.cloudvault.auth.repository;

import com.cloudvault.auth.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    List<UserSession> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
