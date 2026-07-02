package com.cloudvault.core.controller;

import com.cloudvault.core.aspect.LogAction;
import com.cloudvault.core.dto.FileMetadataRequest;
import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.FileService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/metadata")
    @LogAction("FILE_CREATE")
    public ResponseEntity<ApiResponse<FileResponse>> createFileMetadata(@Valid @RequestBody FileMetadataRequest request,
                                                                        @AuthenticationPrincipal UserPrincipal principal) {
        FileResponse response = fileService.createFileMetadata(
                request.getName(),
                request.getFolderId(),
                request.getWorkspaceId(),
                request.getMimeType(),
                request.getSize(),
                request.getChecksumSha256(),
                principal.getId()
        );
        return new ResponseEntity<>(ApiResponse.success(response, "File metadata registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<ApiResponse<FileResponse>> getFileMetadata(@PathVariable("fileId") UUID fileId,
                                                                     @AuthenticationPrincipal UserPrincipal principal) {
        FileResponse response = fileService.getFileMetadata(fileId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File metadata retrieved successfully"));
    }

    @GetMapping("/dedup")
    public ResponseEntity<ApiResponse<FileResponse>> getDeduplicatedFile(@RequestParam("sha256") String sha256,
                                                                         @RequestParam("size") long size,
                                                                         @AuthenticationPrincipal UserPrincipal principal) {
        FileResponse response = fileService.findDeduplicatedFile(sha256, size, principal.getId());
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "Deduplicated file found"));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "No duplicate file content found"));
    }

    @PatchMapping("/{fileId}/rename")
    @LogAction("FILE_RENAME")
    public ResponseEntity<ApiResponse<FileResponse>> renameFile(@PathVariable("fileId") UUID fileId,
                                                                @RequestParam("name") String name,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        FileResponse response = fileService.renameFile(fileId, name, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File renamed successfully"));
    }

    @PatchMapping("/{fileId}/move")
    @LogAction("FILE_MOVE")
    public ResponseEntity<ApiResponse<FileResponse>> moveFile(@PathVariable("fileId") UUID fileId,
                                                              @RequestParam(value = "targetFolderId", required = false) UUID targetFolderId,
                                                              @AuthenticationPrincipal UserPrincipal principal) {
        FileResponse response = fileService.moveFile(fileId, targetFolderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File moved successfully"));
    }

    @DeleteMapping("/{fileId}")
    @LogAction("FILE_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable("fileId") UUID fileId,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        fileService.deleteFile(fileId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted successfully"));
    }
}
