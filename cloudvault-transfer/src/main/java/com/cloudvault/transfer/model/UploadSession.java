package com.cloudvault.transfer.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UploadSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uploadId;
    private String fileName;
    private long size;
    private UUID folderId;
    private UUID workspaceId;
    private String sha256;
    private int totalChunks;
    private String mimeType;
    private UUID fileId;
    private Set<Integer> completedChunks = new HashSet<>();

    public UploadSession() {
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
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

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public Set<Integer> getCompletedChunks() {
        return completedChunks;
    }

    public void setCompletedChunks(Set<Integer> completedChunks) {
        this.completedChunks = completedChunks;
    }
}
