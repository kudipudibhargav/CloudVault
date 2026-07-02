package com.cloudvault.core.service;

import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.model.File;
import com.cloudvault.core.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final FileRepository fileRepository;
    private final WorkspaceService workspaceService;
    private final FileService fileService;

    public SearchService(FileRepository fileRepository, WorkspaceService workspaceService, FileService fileService) {
        this.fileRepository = fileRepository;
        this.workspaceService = workspaceService;
        this.fileService = fileService;
    }

    @Transactional(readOnly = true)
    public List<FileResponse> searchWorkspaceFiles(UUID workspaceId, String query, String mimeType,
                                                   UUID creatorId, String tag, UUID userId) {
        // Enforce workspace membership authorization
        workspaceService.getWorkspaceRole(workspaceId, userId);

        // Convert empty search fields to null for JPQL query mapping
        String queryStr = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String mimeTypeStr = (mimeType != null && !mimeType.trim().isEmpty()) ? mimeType.trim() : null;
        String tagStr = (tag != null && !tag.trim().isEmpty()) ? tag.trim() : null;

        List<File> files = fileRepository.searchFiles(workspaceId, queryStr, mimeTypeStr, creatorId, tagStr);

        return files.stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());
    }

    private FileResponse mapToFileResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .folderId(file.getFolder() != null ? file.getFolder().getId() : null)
                .workspaceId(file.getWorkspace().getId())
                .creatorId(file.getCreatorId())
                .mimeType(file.getMimeType())
                .size(file.getSize())
                .currentVersionId(file.getCurrentVersionId())
                .checksumSha256(file.getChecksumSha256())
                .aiSummary(file.getAiSummary())
                .tags(file.getTags())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
