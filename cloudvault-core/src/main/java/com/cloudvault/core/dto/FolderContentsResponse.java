package com.cloudvault.core.dto;

import java.util.List;

public class FolderContentsResponse {
    private List<FolderResponse> folders;
    private List<FileResponse> files;

    public FolderContentsResponse() {
    }

    public FolderContentsResponse(List<FolderResponse> folders, List<FileResponse> files) {
        this.folders = folders;
        this.files = files;
    }

    public List<FolderResponse> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderResponse> folders) {
        this.folders = folders;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<FileResponse> files) {
        this.files = files;
    }
}
