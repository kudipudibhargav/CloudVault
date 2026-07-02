package com.cloudvault.core.service;

import com.cloudvault.core.dto.FileVersionResponse;
import com.cloudvault.core.model.File;
import com.cloudvault.core.model.FileVersion;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.FileRepository;
import com.cloudvault.core.repository.FileVersionRepository;
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
public class VersionService {

    private static final Logger log = LoggerFactory.getLogger(VersionService.class);

    private final FileVersionRepository versionRepository;
    private final FileRepository fileRepository;
    private final WorkspaceService workspaceService;

    public VersionService(FileVersionRepository versionRepository, FileRepository fileRepository, WorkspaceService workspaceService) {
        this.versionRepository = versionRepository;
        this.fileRepository = fileRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public FileVersionResponse createFileVersion(UUID fileId, String versionHash, long size, UUID uploaderId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Determine next version number
        int nextVersion = versionRepository.findFirstByFileIdOrderByVersionNumberDesc(fileId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        String storagePath = "files/" + fileId + "/v" + nextVersion;

        FileVersion version = FileVersion.builder()
                .file(file)
                .versionHash(versionHash)
                .versionNumber(nextVersion)
                .storagePath(storagePath)
                .size(size)
                .uploaderId(uploaderId)
                .build();

        FileVersion savedVersion = versionRepository.save(version);

        // Update active pointer on main file record
        file.setCurrentVersionId(String.valueOf(nextVersion));
        file.setChecksumSha256(versionHash);
        file.setSize(size);
        fileRepository.save(file);

        log.info("FileVersion created: v{} for file: {} (SHA256: {})", nextVersion, fileId, versionHash);
        return mapToVersionResponse(savedVersion);
    }

    @Transactional(readOnly = true)
    public List<FileVersionResponse> getFileHistory(UUID fileId, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Verify authorization
        workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);

        List<FileVersion> history = versionRepository.findByFileIdOrderByVersionNumberDesc(fileId);
        return history.stream()
                .map(this::mapToVersionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FileVersionResponse rollbackFileVersion(UUID fileId, int versionNumber, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Enforce write access
        WorkspaceRole role = workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);
        if (role == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot perform version rollbacks");
        }

        FileVersion targetVersion = versionRepository.findByFileIdAndVersionNumber(fileId, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Target version v" + versionNumber + " not found"));

        // Update main file pointer to target version properties
        file.setCurrentVersionId(String.valueOf(targetVersion.getVersionNumber()));
        file.setChecksumSha256(targetVersion.getVersionHash());
        file.setSize(targetVersion.getSize());
        fileRepository.save(file);

        log.info("Rolled back file: {} to version v{}", fileId, versionNumber);
        return mapToVersionResponse(targetVersion);
    }

    public FileVersionResponse mapToVersionResponse(FileVersion version) {
        return FileVersionResponse.builder()
                .id(version.getId())
                .fileId(version.getFile().getId())
                .versionHash(version.getVersionHash())
                .versionNumber(version.getVersionNumber())
                .storagePath(version.getStoragePath())
                .size(version.getSize())
                .uploaderId(version.getUploaderId())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
