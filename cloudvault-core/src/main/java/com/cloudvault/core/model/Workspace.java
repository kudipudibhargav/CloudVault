package com.cloudvault.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "storage_quota", nullable = false)
    private long storageQuota = 10737418240L; // Default 10GB in bytes

    @Column(name = "storage_used", nullable = false)
    private long storageUsed = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Workspace() {
    }

    public Workspace(UUID id, String name, UUID ownerId, long storageQuota, long storageUsed, LocalDateTime createdAt) {
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
        private long storageQuota = 10737418240L;
        private long storageUsed = 0L;
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

        public Workspace build() {
            return new Workspace(id, name, ownerId, storageQuota, storageUsed, createdAt);
        }
    }
}
