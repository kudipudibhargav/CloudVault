package com.cloudvault.core.controller;

import com.cloudvault.core.aspect.LogAction;
import com.cloudvault.core.dto.CommentRequest;
import com.cloudvault.core.dto.CommentResponse;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.CommentService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files/{fileId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @LogAction("COMMENT_ADD")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable("fileId") UUID fileId,
                                                                   @Valid @RequestBody CommentRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal principal) {
        CommentResponse response = commentService.addComment(fileId, request, principal.getId(), principal.getEmail());
        return new ResponseEntity<>(ApiResponse.success(response, "Comment added successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable("fileId") UUID fileId,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        List<CommentResponse> response = commentService.getFileComments(fileId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Comments retrieved successfully"));
    }
}
