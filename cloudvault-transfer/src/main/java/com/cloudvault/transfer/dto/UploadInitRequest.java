package com.cloudvault.transfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class UploadInitRequest {

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotNull(message = "File size is required")
    private Long size;

    private UUID folderId;

    @NotNull(message = "Workspace ID is required")
    private UUID workspaceId;

    @NotBlank(message = "SHA-256 hash is required")
    private String sha256;

    @NotNull(message = "Total chunks count is required")
    private Integer totalChunks;

    @NotBlank(message = "MIME type is required")
    private String mimeType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
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

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
