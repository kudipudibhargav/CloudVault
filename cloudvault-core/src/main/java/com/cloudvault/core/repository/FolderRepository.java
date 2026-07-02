package com.cloudvault.core.repository;

import com.cloudvault.core.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    List<Folder> findByWorkspaceIdAndParentIdAndIsDeleted(UUID workspaceId, UUID parentId, boolean isDeleted);
    List<Folder> findByWorkspaceIdAndParentIsNullAndIsDeleted(UUID workspaceId, boolean isDeleted);
    List<Folder> findByPathMaterializedStartingWith(String pathPrefix);

    @Modifying
    @Query("UPDATE Folder f SET f.pathMaterialized = REPLACE(f.pathMaterialized, :oldPath, :newPath) WHERE f.pathMaterialized LIKE :oldPathLike")
    void updateChildPaths(String oldPath, String newPath, String oldPathLike);

    Optional<Folder> findByIdAndIsDeleted(UUID id, boolean isDeleted);
}
