package com.cloudvault.core.repository;

import com.cloudvault.core.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findByWorkspaceIdAndFolderIdAndIsDeleted(UUID workspaceId, UUID folderId, boolean isDeleted);
    List<File> findByWorkspaceIdAndFolderIsNullAndIsDeleted(UUID workspaceId, boolean isDeleted);
    Optional<File> findByIdAndIsDeleted(UUID id, boolean isDeleted);
    Optional<File> findFirstByChecksumSha256AndSizeAndIsDeleted(String checksumSha256, long size, boolean isDeleted);

    @Query("SELECT DISTINCT f FROM File f LEFT JOIN f.tags t " +
           "WHERE f.workspace.id = :workspaceId AND f.isDeleted = false " +
           "AND (:query IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.aiSummary) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:mimeType IS NULL OR f.mimeType = :mimeType) " +
           "AND (:creatorId IS NULL OR f.creatorId = :creatorId) " +
           "AND (:tag IS NULL OR t = :tag)")
    List<File> searchFiles(@Param("workspaceId") UUID workspaceId,
                           @Param("query") String query,
                           @Param("mimeType") String mimeType,
                           @Param("creatorId") UUID creatorId,
                           @Param("tag") String tag);
}
