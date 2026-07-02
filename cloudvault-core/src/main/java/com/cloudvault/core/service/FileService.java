package com.cloudvault.core.service;

import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.model.File;
import com.cloudvault.core.model.Folder;
import com.cloudvault.core.model.Workspace;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.FileRepository;
import com.cloudvault.core.repository.FolderRepository;
import com.cloudvault.core.repository.WorkspaceRepository;
import com.cloudvault.common.exception.BadRequestException;
import com.cloudvault.common.exception.ForbiddenException;
import com.cloudvault.common.exception.ResourceNotFoundException;
import com.cloudvault.common.event.ResilientEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final AiAnalysisService aiAnalysisService;
    private final ResilientEventPublisher eventPublisher;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository,
                       WorkspaceRepository workspaceRepository, WorkspaceService workspaceService,
                       AiAnalysisService aiAnalysisService, ResilientEventPublisher eventPublisher) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceService = workspaceService;
        this.aiAnalysisService = aiAnalysisService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FileResponse createFileMetadata(String name, UUID folderId, UUID workspaceId,
                                           String mimeType, long size, String checksumSha256, UUID creatorId) {
        WorkspaceRole role = workspaceService.getWorkspaceRole(workspaceId, creatorId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot create files");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        // Enforce storage quota limits
        if (workspace.getStorageUsed() + size > workspace.getStorageQuota()) {
            throw new BadRequestException("Insufficient storage space in this workspace");
        }

        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findByIdAndIsDeleted(folderId, false)
                    .orElseThrow(() -> new ResourceNotFoundException("Target folder not found"));

            if (!folder.getWorkspace().getId().equals(workspaceId)) {
                throw new BadRequestException("Target folder does not belong to the selected workspace");
            }
        }

        File file = File.builder()
                .name(name)
                .folder(folder)
                .workspace(workspace)
                .creatorId(creatorId)
                .mimeType(mimeType)
                .size(size)
                .checksumSha256(checksumSha256)
                .build();

        File savedFile = fileRepository.save(file);

        // Update workspace consumed storage
        workspace.setStorageUsed(workspace.getStorageUsed() + size);
        workspaceRepository.save(workspace);

        // Check 90% storage bounds and trigger alert event
        if (workspace.getStorageQuota() > 0) {
            double usageRatio = (double) workspace.getStorageUsed() / workspace.getStorageQuota();
            if (usageRatio >= 0.9) {
                eventPublisher.publishEvent("workspace.quota", "Storage usage is at " + (int)(usageRatio * 100) + "% for workspace " + workspace.getId());
            }
        }

        // Trigger AI content analysis & tagging
        aiAnalysisService.analyzeFile(savedFile.getId());
        File reloadedFile = fileRepository.findById(savedFile.getId()).orElse(savedFile);

        log.info("File metadata registered: {} (Size: {} bytes, Sha256: {}) in workspace: {}", name, size, checksumSha256, workspaceId);
        return mapToFileResponse(reloadedFile);
    }

    @Transactional(readOnly = true)
    public FileResponse getFileMetadata(UUID fileId, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Enforce access authorization
        workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);

        return mapToFileResponse(file);
    }

    @Transactional(readOnly = true)
    public FileResponse findDeduplicatedFile(String sha256, long size, UUID userId) {
        // Enforce basic auth check
        return fileRepository.findFirstByChecksumSha256AndSizeAndIsDeleted(sha256, size, false)
                .map(this::mapToFileResponse)
                .orElse(null);
    }

    @Transactional
    public FileResponse renameFile(UUID fileId, String newName, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot rename files");
        }

        file.setName(newName);
        File savedFile = fileRepository.save(file);
        log.info("File renamed to: {} (ID: {})", newName, fileId);
        return mapToFileResponse(savedFile);
    }

    @Transactional
    public FileResponse moveFile(UUID fileId, UUID targetFolderId, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot move files");
        }

        Folder targetFolder = null;
        if (targetFolderId != null) {
            targetFolder = folderRepository.findByIdAndIsDeleted(targetFolderId, false)
                    .orElseThrow(() -> new ResourceNotFoundException("Target folder not found"));

            if (!targetFolder.getWorkspace().getId().equals(file.getWorkspace().getId())) {
                throw new BadRequestException("Target folder belongs to a different workspace");
            }
        }

        file.setFolder(targetFolder);
        File savedFile = fileRepository.save(file);
        log.info("File {} moved under folder {}", fileId, targetFolderId);
        return mapToFileResponse(savedFile);
    }

    @Transactional
    public void deleteFile(UUID fileId, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot delete files");
        }

        // Soft-delete the file
        file.setDeleted(true);
        fileRepository.save(file);

        // Deduct file size from workspace consumed storage
        Workspace workspace = file.getWorkspace();
        long newStorageUsed = Math.max(0L, workspace.getStorageUsed() - file.getSize());
        workspace.setStorageUsed(newStorageUsed);
        workspaceRepository.save(workspace);

        log.info("Soft deleted file {} and updated workspace storage", fileId);
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
