package com.cloudvault.core.service;

import com.cloudvault.core.dto.WorkspaceMemberRequest;
import com.cloudvault.core.dto.WorkspaceMemberResponse;
import com.cloudvault.core.dto.WorkspaceRequest;
import com.cloudvault.core.dto.WorkspaceResponse;
import com.cloudvault.core.model.Workspace;
import com.cloudvault.core.model.WorkspaceMember;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.WorkspaceMemberRepository;
import com.cloudvault.core.repository.WorkspaceRepository;
import com.cloudvault.common.exception.ConflictException;
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
public class WorkspaceService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceService.class);

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository memberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, UUID ownerId) {
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .ownerId(ownerId)
                .storageQuota(request.getStorageQuota() != null ? request.getStorageQuota() : 10737418240L)
                .storageUsed(0L)
                .build();

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        // Add owner as a member
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspace(savedWorkspace)
                .userId(ownerId)
                .role(WorkspaceRole.OWNER)
                .build();
        memberRepository.save(ownerMember);

        log.info("Workspace created: {} by Owner: {}", savedWorkspace.getName(), ownerId);
        return mapToWorkspaceResponse(savedWorkspace);
    }

    @Transactional
    public WorkspaceMemberResponse addMember(UUID workspaceId, WorkspaceMemberRequest request, UUID requesterId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        // Enforce requester is OWNER of the workspace
        WorkspaceRole requesterRole = getWorkspaceRole(workspaceId, requesterId);
        if (requesterRole != WorkspaceRole.OWNER) {
            throw new ForbiddenException("Only workspace owners can manage members");
        }

        if (memberRepository.existsByWorkspaceIdAndUserId(workspaceId, request.getUserId())) {
            throw new ConflictException("User is already a member of this workspace");
        }

        WorkspaceMember newMember = WorkspaceMember.builder()
                .workspace(workspace)
                .userId(request.getUserId())
                .role(WorkspaceRole.valueOf(request.getRole().toUpperCase()))
                .build();

        WorkspaceMember savedMember = memberRepository.save(newMember);
        log.info("Added user {} to workspace {} with role {}", request.getUserId(), workspaceId, request.getRole());

        return mapToMemberResponse(savedMember);
    }

    @Transactional
    public void removeMember(UUID workspaceId, UUID memberUserId, UUID requesterId) {
        WorkspaceMember member = memberRepository.findByWorkspaceIdAndUserId(workspaceId, memberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace membership not found"));

        // Enforce requester is OWNER or the user themselves leaving
        WorkspaceRole requesterRole = getWorkspaceRole(workspaceId, requesterId);
        boolean isSelfLeaving = memberUserId.equals(requesterId);

        if (requesterRole != WorkspaceRole.OWNER && !isSelfLeaving) {
            throw new ForbiddenException("Only workspace owners can remove members");
        }

        if (member.getRole() == WorkspaceRole.OWNER && isSelfLeaving) {
            throw new ConflictException("Owners cannot leave workspaces without transferring ownership first");
        }

        memberRepository.delete(member);
        log.info("Removed user {} from workspace {}", memberUserId, workspaceId);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getUserWorkspaces(UUID userId) {
        List<WorkspaceMember> memberships = memberRepository.findByUserId(userId);
        return memberships.stream()
                .map(WorkspaceMember::getWorkspace)
                .map(this::mapToWorkspaceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspace(UUID workspaceId, UUID userId) {
        // Enforce membership
        getWorkspaceRole(workspaceId, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return mapToWorkspaceResponse(workspace);
    }

    public WorkspaceRole getWorkspaceRole(UUID workspaceId, UUID userId) {
        return memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .map(WorkspaceMember::getRole)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this workspace"));
    }

    private WorkspaceResponse mapToWorkspaceResponse(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .ownerId(workspace.getOwnerId())
                .storageQuota(workspace.getStorageQuota())
                .storageUsed(workspace.getStorageUsed())
                .createdAt(workspace.getCreatedAt())
                .build();
    }

    private WorkspaceMemberResponse mapToMemberResponse(WorkspaceMember member) {
        return WorkspaceMemberResponse.builder()
                .id(member.getId())
                .workspaceId(member.getWorkspace().getId())
                .userId(member.getUserId())
                .role(member.getRole().name())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
