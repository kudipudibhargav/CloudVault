package com.cloudvault.transfer.dto;

import java.util.UUID;

public class UploadInitResponse {
    private String uploadId;
    private long chunkSize = 5242880L; // Default 5MB chunks
    private boolean deduplicated;
    private UUID fileId;

    public UploadInitResponse() {
    }

    public UploadInitResponse(String uploadId, long chunkSize, boolean deduplicated, UUID fileId) {
        this.uploadId = uploadId;
        this.chunkSize = chunkSize;
        this.deduplicated = deduplicated;
        this.fileId = fileId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public boolean isDeduplicated() {
        return deduplicated;
    }

    public void setDeduplicated(boolean deduplicated) {
        this.deduplicated = deduplicated;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }
}
