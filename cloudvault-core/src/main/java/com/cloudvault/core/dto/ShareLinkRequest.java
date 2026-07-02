package com.cloudvault.core.dto;

import java.util.UUID;

public class ShareLinkRequest {
    private UUID fileId;
    private UUID folderId;
    private String password;
    private Integer expiresAtDays;
    private Integer downloadLimit = -1;
    private String permissionLevel = "VIEWER"; // VIEWER, EDITOR

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) {
        this.folderId = folderId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getExpiresAtDays() {
        return expiresAtDays;
    }

    public void setExpiresAtDays(Integer expiresAtDays) {
        this.expiresAtDays = expiresAtDays;
    }

    public Integer getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(Integer downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
