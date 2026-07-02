package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class FileVersionResponse {
    private UUID id;
    private UUID fileId;
    private String versionHash;
    private int versionNumber;
    private String storagePath;
    private long size;
    private UUID uploaderId;
    private LocalDateTime createdAt;

    public FileVersionResponse() {
    }

    public FileVersionResponse(UUID id, UUID fileId, String versionHash, int versionNumber,
                              String storagePath, long size, UUID uploaderId, LocalDateTime createdAt) {
        this.id = id;
        this.fileId = fileId;
        this.versionHash = versionHash;
        this.versionNumber = versionNumber;
        this.storagePath = storagePath;
        this.size = size;
        this.uploaderId = uploaderId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getVersionHash() {
        return versionHash;
    }

    public void setVersionHash(String versionHash) {
        this.versionHash = versionHash;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public UUID getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(UUID uploaderId) {
        this.uploaderId = uploaderId;
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
        private UUID fileId;
        private String versionHash;
        private int versionNumber;
        private String storagePath;
        private long size;
        private UUID uploaderId;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder versionHash(String versionHash) {
            this.versionHash = versionHash;
            return this;
        }

        public Builder versionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
            return this;
        }

        public Builder storagePath(String storagePath) {
            this.storagePath = storagePath;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder uploaderId(UUID uploaderId) {
            this.uploaderId = uploaderId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FileVersionResponse build() {
            return new FileVersionResponse(id, fileId, versionHash, versionNumber, storagePath, size, uploaderId, createdAt);
        }
    }
}
