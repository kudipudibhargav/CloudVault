package com.cloudvault.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "files", indexes = {
        @Index(name = "idx_files_workspace", columnList = "workspace_id"),
        @Index(name = "idx_files_folder", columnList = "folder_id"),
        @Index(name = "idx_files_sha256", columnList = "checksum_sha256")
})
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long size = 0L;

    @Column(name = "current_version_id")
    private String currentVersionId;

    @Column(name = "checksum_sha256")
    private String checksumSha256;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "ai_summary", length = 2048)
    private String aiSummary;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_tags", joinColumns = @JoinColumn(name = "file_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public File() {
    }

    public File(UUID id, String name, Folder folder, Workspace workspace, UUID creatorId,
                String mimeType, long size, String currentVersionId, String checksumSha256,
                boolean isDeleted, String aiSummary, Set<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.folder = folder;
        this.workspace = workspace;
        this.creatorId = creatorId;
        this.mimeType = mimeType;
        this.size = size;
        this.currentVersionId = currentVersionId;
        this.checksumSha256 = checksumSha256;
        this.isDeleted = isDeleted;
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

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
        private Folder folder;
        private Workspace workspace;
        private UUID creatorId;
        private String mimeType;
        private long size = 0L;
        private String currentVersionId;
        private String checksumSha256;
        private boolean isDeleted = false;
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

        public Builder folder(Folder folder) {
            this.folder = folder;
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

        public Builder isDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
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

        public File build() {
            return new File(id, name, folder, workspace, creatorId, mimeType, size, currentVersionId, checksumSha256, isDeleted, aiSummary, tags, createdAt, updatedAt);
        }
    }
}
