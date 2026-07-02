package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkspaceResponse {
    private UUID id;
    private String name;
    private UUID ownerId;
    private long storageQuota;
    private long storageUsed;
    private LocalDateTime createdAt;

    public WorkspaceResponse() {
    }

    public WorkspaceResponse(UUID id, String name, UUID ownerId, long storageQuota, long storageUsed, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.storageQuota = storageQuota;
        this.storageUsed = storageUsed;
        this.createdAt = createdAt;
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

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public long getStorageQuota() {
        return storageQuota;
    }

    public void setStorageQuota(long storageQuota) {
        this.storageQuota = storageQuota;
    }

    public long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private UUID ownerId;
        private long storageQuota;
        private long storageUsed;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerId(UUID ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder storageQuota(long storageQuota) {
            this.storageQuota = storageQuota;
            return this;
        }

        public Builder storageUsed(long storageUsed) {
            this.storageUsed = storageUsed;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WorkspaceResponse build() {
            return new WorkspaceResponse(id, name, ownerId, storageQuota, storageUsed, createdAt);
        }
    }
}
