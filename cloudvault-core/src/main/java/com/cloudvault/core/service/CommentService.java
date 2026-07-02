package com.cloudvault.core.service;

import com.cloudvault.core.dto.CommentRequest;
import com.cloudvault.core.dto.CommentResponse;
import com.cloudvault.core.model.Comment;
import com.cloudvault.core.model.File;
import com.cloudvault.core.repository.CommentRepository;
import com.cloudvault.core.repository.FileRepository;
import com.cloudvault.common.exception.BadRequestException;
import com.cloudvault.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final WorkspaceService workspaceService;

    public CommentService(CommentRepository commentRepository, FileRepository fileRepository, WorkspaceService workspaceService) {
        this.commentRepository = commentRepository;
        this.fileRepository = fileRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public CommentResponse addComment(UUID fileId, CommentRequest request, UUID authorId, String authorName) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Verify authorization
        workspaceService.getWorkspaceRole(file.getWorkspace().getId(), authorId);

        if (request.getParentCommentId() != null) {
            commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment thread not found"));
        }

        Comment comment = Comment.builder()
                .file(file)
                .authorId(authorId)
                .authorName(authorName)
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Parse @mentions
        List<String> mentions = parseMentions(request.getContent());
        for (String username : mentions) {
            log.info("NOTIFICATION: User '{}' mentioned '{}' in file comment on file '{}'", authorName, username, fileId);
        }

        return mapToCommentResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getFileComments(UUID fileId, UUID userId) {
        File file = fileRepository.findByIdAndIsDeleted(fileId, false)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Verify authorization
        workspaceService.getWorkspaceRole(file.getWorkspace().getId(), userId);

        List<Comment> comments = commentRepository.findByFileIdOrderByCreatedAtAsc(fileId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private List<String> parseMentions(String text) {
        List<String> mentions = new ArrayList<>();
        if (text == null) return mentions;

        Pattern pattern = Pattern.compile("@([a-zA-Z0-9_.-]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return mentions;
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .fileId(comment.getFile().getId())
                .authorId(comment.getAuthorId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
