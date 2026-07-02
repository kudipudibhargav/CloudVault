package com.cloudvault.core.controller;

import com.cloudvault.core.aspect.LogAction;
import com.cloudvault.core.dto.ShareLinkRequest;
import com.cloudvault.core.dto.ShareLinkResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.ShareService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shares")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    @LogAction("SHARE_LINK_CREATE")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> createShareLink(@Valid @RequestBody ShareLinkRequest request,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        ShareLinkResponse response = shareService.createShareLink(request, principal.getId());
        return new ResponseEntity<>(ApiResponse.success(response, "Share link generated successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/{token}/resolve")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> resolveShareLink(@PathVariable("token") String token,
                                                                           @RequestParam(value = "password", required = false) String password) {
        ShareLinkResponse response = shareService.resolveShareLink(token, password);
        return ResponseEntity.ok(ApiResponse.success(response, "Share link resolved successfully"));
    }
}
