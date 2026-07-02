package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShareLinkResponse {
    private UUID id;
    private UUID fileId;
    private UUID folderId;
    private UUID creatorId;
    private String accessToken;
    private LocalDateTime expiresAt;
    private int downloadLimit;
    private int downloadCount;
    private String permissionLevel;
    private boolean isPasswordProtected;
    private LocalDateTime createdAt;

    public ShareLinkResponse() {
    }

    public ShareLinkResponse(UUID id, UUID fileId, UUID folderId, UUID creatorId, String accessToken,
                             LocalDateTime expiresAt, int downloadLimit, int downloadCount,
                             String permissionLevel, boolean isPasswordProtected, LocalDateTime createdAt) {
        this.id = id;
        this.fileId = fileId;
        this.folderId = folderId;
        this.creatorId = creatorId;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        this.downloadLimit = downloadLimit;
        this.downloadCount = downloadCount;
        this.permissionLevel = permissionLevel;
        this.isPasswordProtected = isPasswordProtected;
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

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) {
        this.folderId = folderId;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public boolean isPasswordProtected() {
        return isPasswordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        isPasswordProtected = passwordProtected;
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
        private UUID folderId;
        private UUID creatorId;
        private String accessToken;
        private LocalDateTime expiresAt;
        private int downloadLimit;
        private int downloadCount;
        private String permissionLevel;
        private boolean isPasswordProtected;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder folderId(UUID folderId) {
            this.folderId = folderId;
            return this;
        }

        public Builder creatorId(UUID creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder downloadLimit(int downloadLimit) {
            this.downloadLimit = downloadLimit;
            return this;
        }

        public Builder downloadCount(int downloadCount) {
            this.downloadCount = downloadCount;
            return this;
        }

        public Builder permissionLevel(String permissionLevel) {
            this.permissionLevel = permissionLevel;
            return this;
        }

        public Builder isPasswordProtected(boolean isPasswordProtected) {
            this.isPasswordProtected = isPasswordProtected;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShareLinkResponse build() {
            return new ShareLinkResponse(id, fileId, folderId, creatorId, accessToken, expiresAt, downloadLimit, downloadCount, permissionLevel, isPasswordProtected, createdAt);
        }
    }
}
