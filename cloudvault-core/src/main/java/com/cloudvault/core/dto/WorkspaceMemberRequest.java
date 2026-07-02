package com.cloudvault.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class WorkspaceMemberRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Role is required")
    private String role; // OWNER, EDITOR, VIEWER

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
}
