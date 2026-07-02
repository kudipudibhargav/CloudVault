package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileResponse {
    private UUID id;
    private String name;
    private UUID folderId;
    private UUID workspaceId;
    private UUID creatorId;
    private String mimeType;
    private long size;
    private String currentVersionId;
    private String checksumSha256;
    private String aiSummary;
    private Set<String> tags = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FileResponse() {
    }

    public FileResponse(UUID id, String name, UUID folderId, UUID workspaceId, UUID creatorId,
                        String mimeType, long size, String currentVersionId, String checksumSha256,
                        String aiSummary, Set<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.folderId = folderId;
        this.workspaceId = workspaceId;
        this.creatorId = creatorId;
        this.mimeType = mimeType;
        this.size = size;
        this.currentVersionId = currentVersionId;
        this.checksumSha256 = checksumSha256;
        this.aiSummary = aiSummary;
        this.tags = tags != null ? tags : new HashSet<>();
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

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) {
        this.folderId = folderId;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCurrentVersionId() {
        return currentVersionId;
    }

    public void setCurrentVersionId(String currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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
        private UUID folderId;
        private UUID workspaceId;
        private UUID creatorId;
        private String mimeType;
        private long size;
        private String currentVersionId;
        private String checksumSha256;
        private String aiSummary;
        private Set<String> tags = new HashSet<>();
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

        public Builder folderId(UUID folderId) {
            this.folderId = folderId;
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

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder currentVersionId(String currentVersionId) {
            this.currentVersionId = currentVersionId;
            return this;
        }

        public Builder checksumSha256(String checksumSha256) {
            this.checksumSha256 = checksumSha256;
            return this;
        }

        public Builder aiSummary(String aiSummary) {
            this.aiSummary = aiSummary;
            return this;
        }

        public Builder tags(Set<String> tags) {
            this.tags = tags;
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

        public FileResponse build() {
            return new FileResponse(id, name, folderId, workspaceId, creatorId, mimeType, size, currentVersionId, checksumSha256, aiSummary, tags, createdAt, updatedAt);
        }
    }
}
