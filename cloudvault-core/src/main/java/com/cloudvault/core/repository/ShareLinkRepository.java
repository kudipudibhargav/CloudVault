package com.cloudvault.core.repository;

import com.cloudvault.core.model.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, UUID> {
    Optional<ShareLink> findByAccessToken(String accessToken);
    List<ShareLink> findByCreatorId(UUID creatorId);
}
