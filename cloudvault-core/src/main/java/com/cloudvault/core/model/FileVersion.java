package com.cloudvault.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_versions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"file_id", "version_number"})
})
public class FileVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(name = "version_hash", nullable = false)
    private String versionHash;

    @Column(name = "version_number", nullable = false)
    private int versionNumber = 1;

    @Column(name = "storage_path", nullable = false, unique = true)
    private String storagePath;

    @Column(nullable = false)
    private long size = 0L;

    @Column(name = "uploader_id", nullable = false)
    private UUID uploaderId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FileVersion() {
    }

    public FileVersion(UUID id, File file, String versionHash, int versionNumber,
                       String storagePath, long size, UUID uploaderId, LocalDateTime createdAt) {
        this.id = id;
        this.file = file;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
        private File file;
        private String versionHash;
        private int versionNumber = 1;
        private String storagePath;
        private long size;
        private UUID uploaderId;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
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

        public FileVersion build() {
            return new FileVersion(id, file, versionHash, versionNumber, storagePath, size, uploaderId, createdAt);
        }
    }
}
