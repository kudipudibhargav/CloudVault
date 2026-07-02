package com.cloudvault.core.controller;

import com.cloudvault.core.dto.FileVersionResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.VersionService;
import com.cloudvault.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files/{fileId}/versions")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FileVersionResponse>> createVersion(@PathVariable("fileId") UUID fileId,
                                                                          @RequestParam("versionHash") String versionHash,
                                                                          @RequestParam("size") long size,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        FileVersionResponse response = versionService.createFileVersion(fileId, versionHash, size, principal.getId());
        return new ResponseEntity<>(ApiResponse.success(response, "File version registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FileVersionResponse>>> getHistory(@PathVariable("fileId") UUID fileId,
                                                                             @AuthenticationPrincipal UserPrincipal principal) {
        List<FileVersionResponse> response = versionService.getFileHistory(fileId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File version history retrieved successfully"));
    }

    @PostMapping("/{versionNumber}/rollback")
    public ResponseEntity<ApiResponse<FileVersionResponse>> rollbackVersion(@PathVariable("fileId") UUID fileId,
                                                                            @PathVariable("versionNumber") int versionNumber,
                                                                            @AuthenticationPrincipal UserPrincipal principal) {
        FileVersionResponse response = versionService.rollbackFileVersion(fileId, versionNumber, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File rolled back successfully to version v" + versionNumber));
    }
}
