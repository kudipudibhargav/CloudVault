package com.cloudvault.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "folders", indexes = {
        @Index(name = "idx_folders_workspace", columnList = "workspace_id"),
        @Index(name = "idx_folders_path", columnList = "path_materialized")
})
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "path_materialized", nullable = false, length = 1024)
    private String pathMaterialized = "/";

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Folder() {
    }

    public Folder(UUID id, String name, Folder parent, Workspace workspace, UUID creatorId,
                  String pathMaterialized, boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.workspace = workspace;
        this.creatorId = creatorId;
        this.pathMaterialized = pathMaterialized != null ? pathMaterialized : "/";
        this.isDeleted = isDeleted;
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

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
        private Folder parent;
        private Workspace workspace;
        private UUID creatorId;
        private String pathMaterialized = "/";
        private boolean isDeleted = false;
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

        public Builder parent(Folder parent) {
            this.parent = parent;
            return this;
        }

        public Builder workspace(Workspace workspace) {
            this.workspace = workspace;
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

        public Builder isDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
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

        public Folder build() {
            return new Folder(id, name, parent, workspace, creatorId, pathMaterialized, isDeleted, createdAt, updatedAt);
        }
    }
}
