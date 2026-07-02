package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class FolderResponse {
    private UUID id;
    private String name;
    private UUID parentId;
    private UUID workspaceId;
    private UUID creatorId;
    private String pathMaterialized;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FolderResponse() {
    }

    public FolderResponse(UUID id, String name, UUID parentId, UUID workspaceId, UUID creatorId,
                          String pathMaterialized, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.workspaceId = workspaceId;
        this.creatorId = creatorId;
        this.pathMaterialized = pathMaterialized;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public String getPathMaterialized() {
        return pathMaterialized;
    }

    public void setPathMaterialized(String pathMaterialized) {
        this.pathMaterialized = pathMaterialized;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private UUID parentId;
        private UUID workspaceId;
        private UUID creatorId;
        private String pathMaterialized;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentId(UUID parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder workspaceId(UUID workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        public Builder creatorId(UUID creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder pathMaterialized(String pathMaterialized) {
            this.pathMaterialized = pathMaterialized;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public FolderResponse build() {
            return new FolderResponse(id, name, parentId, workspaceId, creatorId, pathMaterialized, createdAt, updatedAt);
        }
    }
}
