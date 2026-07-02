package com.cloudvault.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class FolderCreateRequest {

    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name cannot exceed 100 characters")
    private String name;

    private UUID parentId;

    @NotNull(message = "Workspace ID is required")
    private UUID workspaceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }
}
