package com.cloudvault.core.controller;

import com.cloudvault.core.aspect.LogAction;
import com.cloudvault.core.dto.WorkspaceMemberRequest;
import com.cloudvault.core.dto.WorkspaceMemberResponse;
import com.cloudvault.core.dto.WorkspaceRequest;
import com.cloudvault.core.dto.WorkspaceResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.WorkspaceService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    @LogAction("WORKSPACE_CREATE")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(@Valid @RequestBody WorkspaceRequest request,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        WorkspaceResponse response = workspaceService.createWorkspace(request, principal.getId());
        return new ResponseEntity<>(ApiResponse.success(response, "Workspace created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getMyWorkspaces(@AuthenticationPrincipal UserPrincipal principal) {
        List<WorkspaceResponse> response = workspaceService.getUserWorkspaces(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "User workspaces retrieved successfully"));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspace(@PathVariable("workspaceId") UUID workspaceId,
                                                                        @AuthenticationPrincipal UserPrincipal principal) {
        WorkspaceResponse response = workspaceService.getWorkspace(workspaceId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Workspace retrieved successfully"));
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> addMember(@PathVariable("workspaceId") UUID workspaceId,
                                                                          @Valid @RequestBody WorkspaceMemberRequest request,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        WorkspaceMemberResponse response = workspaceService.addMember(workspaceId, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Member added successfully"));
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable("workspaceId") UUID workspaceId,
                                                           @PathVariable("userId") UUID userId,
                                                           @AuthenticationPrincipal UserPrincipal principal) {
        workspaceService.removeMember(workspaceId, userId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed successfully"));
    }
}
