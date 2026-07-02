package com.cloudvault.core.controller;

import com.cloudvault.core.aspect.LogAction;
import com.cloudvault.core.dto.FolderContentsResponse;
import com.cloudvault.core.dto.FolderCreateRequest;
import com.cloudvault.core.dto.FolderResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.FolderService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    @LogAction("FOLDER_CREATE")
    public ResponseEntity<ApiResponse<FolderResponse>> createFolder(@Valid @RequestBody FolderCreateRequest request,
                                                                    @AuthenticationPrincipal UserPrincipal principal) {
        FolderResponse response = folderService.createFolder(request, principal.getId());
        return new ResponseEntity<>(ApiResponse.success(response, "Folder created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{workspaceId}/contents")
    public ResponseEntity<ApiResponse<FolderContentsResponse>> getFolderContents(@PathVariable("workspaceId") UUID workspaceId,
                                                                                 @RequestParam(value = "folderId", required = false) UUID folderId,
                                                                                 @AuthenticationPrincipal UserPrincipal principal) {
        FolderContentsResponse response = folderService.getFolderContents(workspaceId, folderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Folder contents retrieved successfully"));
    }

    @PatchMapping("/{folderId}/rename")
    @LogAction("FOLDER_RENAME")
    public ResponseEntity<ApiResponse<FolderResponse>> renameFolder(@PathVariable("folderId") UUID folderId,
                                                                    @RequestParam("name") String name,
                                                                    @AuthenticationPrincipal UserPrincipal principal) {
        FolderResponse response = folderService.renameFolder(folderId, name, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Folder renamed successfully"));
    }

    @PatchMapping("/{folderId}/move")
    @LogAction("FOLDER_MOVE")
    public ResponseEntity<ApiResponse<FolderResponse>> moveFolder(@PathVariable("folderId") UUID folderId,
                                                                  @RequestParam(value = "targetParentId", required = false) UUID targetParentId,
                                                                  @AuthenticationPrincipal UserPrincipal principal) {
        FolderResponse response = folderService.moveFolder(folderId, targetParentId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Folder moved successfully"));
    }

    @DeleteMapping("/{folderId}")
    @LogAction("FOLDER_DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable("folderId") UUID folderId,
                                                           @AuthenticationPrincipal UserPrincipal principal) {
        folderService.deleteFolder(folderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Folder deleted successfully"));
    }
}
