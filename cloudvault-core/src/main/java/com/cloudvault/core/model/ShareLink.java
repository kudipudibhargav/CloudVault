package com.cloudvault.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "share_links", indexes = {
        @Index(name = "idx_share_token", columnList = "access_token")
})
public class ShareLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "access_token", nullable = false, unique = true)
    private String accessToken;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "download_limit")
    private int downloadLimit = -1; // -1 represents unlimited

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    @Column(name = "permission_level", nullable = false)
    private String permissionLevel = "VIEWER"; // VIEWER, EDITOR

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ShareLink() {
    }

    public ShareLink(UUID id, File file, Folder folder, UUID creatorId, String accessToken,
                     String passwordHash, LocalDateTime expiresAt, int downloadLimit,
                     int downloadCount, String permissionLevel, LocalDateTime createdAt) {
        this.id = id;
        this.file = file;
        this.folder = folder;
        this.creatorId = creatorId;
        this.accessToken = accessToken;
        this.passwordHash = passwordHash;
        this.expiresAt = expiresAt;
        this.downloadLimit = downloadLimit;
        this.downloadCount = downloadCount;
        this.permissionLevel = permissionLevel;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
        private File file;
        private Folder folder;
        private UUID creatorId;
        private String accessToken;
        private String passwordHash;
        private LocalDateTime expiresAt;
        private int downloadLimit = -1;
        private int downloadCount = 0;
        private String permissionLevel = "VIEWER";
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder folder(Folder folder) {
            this.folder = folder;
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

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShareLink build() {
            return new ShareLink(id, file, folder, creatorId, accessToken, passwordHash, expiresAt, downloadLimit, downloadCount, permissionLevel, createdAt);
        }
    }
}
