package com.cloudvault.core.service;

import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.dto.FolderContentsResponse;
import com.cloudvault.core.dto.FolderCreateRequest;
import com.cloudvault.core.dto.FolderResponse;
import com.cloudvault.core.model.File;
import com.cloudvault.core.model.Folder;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.FileRepository;
import com.cloudvault.core.repository.FolderRepository;
import com.cloudvault.common.exception.BadRequestException;
import com.cloudvault.common.exception.ForbiddenException;
import com.cloudvault.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private static final Logger log = LoggerFactory.getLogger(FolderService.class);

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final WorkspaceService workspaceService;

    public FolderService(FolderRepository folderRepository, FileRepository fileRepository, WorkspaceService workspaceService) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public FolderResponse createFolder(FolderCreateRequest request, UUID creatorId) {
        // Enforce requester is OWNER or EDITOR
        WorkspaceRole role = workspaceService.getWorkspaceRole(request.getWorkspaceId(), creatorId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot create folders");
        }

        Folder parent = null;
        String path = "/";

        if (request.getParentId() != null) {
            parent = folderRepository.findByIdAndIsDeleted(request.getParentId(), false)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));

            if (!parent.getWorkspace().getId().equals(request.getWorkspaceId())) {
                throw new BadRequestException("Parent folder does not belong to the selected workspace");
            }

            path = parent.getPathMaterialized() + parent.getId() + "/";
        }

        Folder folder = Folder.builder()
                .name(request.getName())
                .parent(parent)
                .workspace(parent != null ? parent.getWorkspace() : Folder.builder().id(request.getWorkspaceId()).build().getWorkspace()) // Will be resolved by hibernate
                .creatorId(creatorId)
                .pathMaterialized(path)
                .build();

        // Resolve workspace proxy mapping
        var ws = new com.cloudvault.core.model.Workspace();
        ws.setId(request.getWorkspaceId());
        folder.setWorkspace(ws);

        Folder savedFolder = folderRepository.save(folder);
        log.info("Folder created: {} (ID: {}) in workspace: {}", savedFolder.getName(), savedFolder.getId(), request.getWorkspaceId());

        return mapToFolderResponse(savedFolder);
    }

    @Transactional(readOnly = true)
    public FolderContentsResponse getFolderContents(UUID workspaceId, UUID folderId, UUID userId) {
        // Enforce membership
        workspaceService.getWorkspaceRole(workspaceId, userId);

        List<Folder> folders;
        List<File> files;

        if (folderId == null) {
            folders = folderRepository.findByWorkspaceIdAndParentIsNullAndIsDeleted(workspaceId, false);
            files = fileRepository.findByWorkspaceIdAndFolderIsNullAndIsDeleted(workspaceId, false);
        } else {
            folders = folderRepository.findByWorkspaceIdAndParentIdAndIsDeleted(workspaceId, folderId, false);
            files = fileRepository.findByWorkspaceIdAndFolderIdAndIsDeleted(workspaceId, folderId, false);
        }

        List<FolderResponse> folderResponses = folders.stream()
                .map(this::mapToFolderResponse)
                .collect(Collectors.toList());

        List<FileResponse> fileResponses = files.stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());

        return new FolderContentsResponse(folderResponses, fileResponses);
    }

    @Transactional
    public FolderResponse renameFolder(UUID folderId, String newName, UUID userId) {
        Folder folder = folderRepository.findByIdAndIsDeleted(folderId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(folder.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot rename folders");
        }

        folder.setName(newName);
        Folder savedFolder = folderRepository.save(folder);
        log.info("Folder renamed to: {} (ID: {})", newName, folderId);
        return mapToFolderResponse(savedFolder);
    }

    @Transactional
    public FolderResponse moveFolder(UUID folderId, UUID targetParentId, UUID userId) {
        Folder folder = folderRepository.findByIdAndIsDeleted(folderId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(folder.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot move folders");
        }

        Folder targetParent = null;
        String newPath = "/";

        if (targetParentId != null) {
            targetParent = folderRepository.findByIdAndIsDeleted(targetParentId, false)
                    .orElseThrow(() -> new ResourceNotFoundException("Target parent folder not found"));

            if (!targetParent.getWorkspace().getId().equals(folder.getWorkspace().getId())) {
                throw new BadRequestException("Target parent folder belongs to a different workspace");
            }

            // Circular reference check: targetParent cannot be a child of folder
            String targetPath = targetParent.getPathMaterialized() + targetParent.getId() + "/";
            String currentPathPrefix = folder.getPathMaterialized() + folder.getId() + "/";
            if (targetPath.startsWith(currentPathPrefix) || targetParent.getId().equals(folder.getId())) {
                throw new BadRequestException("Cannot move a folder into itself or one of its subfolders");
            }

            newPath = targetPath;
        }

        // Cache old materialized path prefixes before update
        String oldChildPrefix = folder.getPathMaterialized() + folder.getId() + "/";
        String newChildPrefix = newPath + folder.getId() + "/";

        folder.setParent(targetParent);
        folder.setPathMaterialized(newPath);
        Folder savedFolder = folderRepository.save(folder);

        // Batch update all child paths recursively
        folderRepository.updateChildPaths(oldChildPrefix, newChildPrefix, oldChildPrefix + "%");
        log.info("Folder {} moved under parent folder {}", folderId, targetParentId);

        return mapToFolderResponse(savedFolder);
    }

    @Transactional
    public void deleteFolder(UUID folderId, UUID userId) {
        Folder folder = folderRepository.findByIdAndIsDeleted(folderId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        WorkspaceRole role = workspaceService.getWorkspaceRole(folder.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot delete folders");
        }

        // Soft-delete the folder
        folder.setDeleted(true);
        folderRepository.save(folder);

        // Soft-delete all subfolders recursively
        String pathPrefix = folder.getPathMaterialized() + folder.getId() + "/";
        List<Folder> subfolders = folderRepository.findByPathMaterializedStartingWith(pathPrefix);
        for (Folder f : subfolders) {
            f.setDeleted(true);
            folderRepository.save(f);
        }

        // Soft-delete all files belonging to this folder and its subfolders
        // Files in folder itself:
        List<File> filesInFolder = fileRepository.findByWorkspaceIdAndFolderIdAndIsDeleted(folder.getWorkspace().getId(), folderId, false);
        for (File f : filesInFolder) {
            f.setDeleted(true);
            fileRepository.save(f);
        }

        // Files in subfolders:
        for (Folder sf : subfolders) {
            List<File> filesInSub = fileRepository.findByWorkspaceIdAndFolderIdAndIsDeleted(folder.getWorkspace().getId(), sf.getId(), false);
            for (File f : filesInSub) {
                f.setDeleted(true);
                fileRepository.save(f);
            }
        }

        log.info("Soft deleted folder {} and all recursive contents", folderId);
    }

    public FolderResponse mapToFolderResponse(Folder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .workspaceId(folder.getWorkspace().getId())
                .creatorId(folder.getCreatorId())
                .pathMaterialized(folder.getPathMaterialized())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }

    public FileResponse mapToFileResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .folderId(file.getFolder() != null ? file.getFolder().getId() : null)
                .workspaceId(file.getWorkspace().getId())
                .creatorId(file.getCreatorId())
                .mimeType(file.getMimeType())
                .size(file.getSize())
                .currentVersionId(file.getCurrentVersionId())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
