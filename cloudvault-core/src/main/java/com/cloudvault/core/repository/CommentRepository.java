package com.cloudvault.core.repository;

import com.cloudvault.core.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByFileIdOrderByCreatedAtAsc(UUID fileId);
    List<Comment> findByFileIdAndParentCommentIdOrderByCreatedAtAsc(UUID fileId, UUID parentCommentId);
}
