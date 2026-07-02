package com.cloudvault.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WorkspaceRequest {

    @NotBlank(message = "Workspace name is required")
    @Size(max = 100, message = "Workspace name cannot exceed 100 characters")
    private String name;

    private Long storageQuota;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStorageQuota() {
        return storageQuota;
    }

    public void setStorageQuota(Long storageQuota) {
        this.storageQuota = storageQuota;
    }
}
