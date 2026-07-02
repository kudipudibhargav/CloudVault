package com.cloudvault.core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkspaceMemberResponse {
    private UUID id;
    private UUID workspaceId;
    private UUID userId;
    private String role;
    private LocalDateTime joinedAt;

    public WorkspaceMemberResponse() {
    }

    public WorkspaceMemberResponse(UUID id, UUID workspaceId, UUID userId, String role, LocalDateTime joinedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID workspaceId;
        private UUID userId;
        private String role;
        private LocalDateTime joinedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder workspaceId(UUID workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder joinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }

        public WorkspaceMemberResponse build() {
            return new WorkspaceMemberResponse(id, workspaceId, userId, role, joinedAt);
        }
    }
}
