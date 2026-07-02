package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponse {
    private UUID id;
    private UUID fileId;
    private UUID authorId;
    private String authorName;
    private String content;
    private UUID parentCommentId;
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public CommentResponse(UUID id, UUID fileId, UUID authorId, String authorName, String content, UUID parentCommentId, LocalDateTime createdAt) {
        this.id = id;
        this.fileId = fileId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.parentCommentId = parentCommentId;
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

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
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
        private UUID authorId;
        private String authorName;
        private String content;
        private UUID parentCommentId;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder authorId(UUID authorId) {
            this.authorId = authorId;
            return this;
        }

        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder parentCommentId(UUID parentCommentId) {
            this.parentCommentId = parentCommentId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommentResponse build() {
            return new CommentResponse(id, fileId, authorId, authorName, content, parentCommentId, createdAt);
        }
    }
}
