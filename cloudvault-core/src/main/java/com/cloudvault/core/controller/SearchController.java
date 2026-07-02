package com.cloudvault.core.controller;

import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.SearchService;
import com.cloudvault.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FileResponse>>> searchWorkspace(@PathVariable("workspaceId") UUID workspaceId,
                                                                           @RequestParam(value = "q", required = false) String query,
                                                                           @RequestParam(value = "mimeType", required = false) String mimeType,
                                                                           @RequestParam(value = "creatorId", required = false) UUID creatorId,
                                                                           @RequestParam(value = "tag", required = false) String tag,
                                                                           @AuthenticationPrincipal UserPrincipal principal) {
        List<FileResponse> response = searchService.searchWorkspaceFiles(workspaceId, query, mimeType, creatorId, tag, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Workspace search completed successfully"));
    }
}
