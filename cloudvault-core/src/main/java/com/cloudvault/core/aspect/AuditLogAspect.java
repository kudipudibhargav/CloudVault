package com.cloudvault.core.aspect;

import com.cloudvault.core.dto.FileResponse;
import com.cloudvault.core.dto.FolderResponse;
import com.cloudvault.core.dto.ShareLinkResponse;
import com.cloudvault.core.dto.WorkspaceResponse;
import com.cloudvault.core.model.AuditLog;
import com.cloudvault.core.repository.AuditLogRepository;
import com.cloudvault.core.security.UserPrincipal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning(pointcut = "@annotation(logAction)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, LogAction logAction, Object result) {
        try {
            // Resolve current user principal
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            UUID userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
                userId = principal.getId();
            }

            String action = logAction.value();
            String entityType = null;
            UUID entityId = null;
            String details = "";

            // Unwrap ResponseEntity if present
            Object unwrappedResult = result;
            if (result instanceof ResponseEntity<?> responseEntity) {
                unwrappedResult = responseEntity.getBody();
            }

            // Extract data depending on DTO type
            if (unwrappedResult instanceof com.cloudvault.common.response.ApiResponse<?> apiResponse) {
                Object data = apiResponse.getData();
                if (data instanceof WorkspaceResponse ws) {
                    entityType = "WORKSPACE";
                    entityId = ws.getId();
                    details = "Workspace '" + ws.getName() + "' created/modified by Owner: " + ws.getOwnerId();
                } else if (data instanceof FolderResponse folder) {
                    entityType = "FOLDER";
                    entityId = folder.getId();
                    details = "Folder '" + folder.getName() + "' created/modified. Materialized Path: " + folder.getPathMaterialized();
                } else if (data instanceof FileResponse file) {
                    entityType = "FILE";
                    entityId = file.getId();
                    details = "File '" + file.getName() + "' metadata modified/registered. Size: " + file.getSize() + " bytes";
                } else if (data instanceof ShareLinkResponse share) {
                    entityType = "SHARE_LINK";
                    entityId = share.getId();
                    details = "Share link generated with token. Permission: " + share.getPermissionLevel() + ". Expiry: " + share.getExpiresAt();
                }
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .ipAddress("127.0.0.1")
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log persisted: Action: {}, User: {}, EntityType: {}, EntityId: {}", action, userId, entityType, entityId);

        } catch (Exception e) {
            log.error("Failed to persist audit log in aspect interceptor", e);
        }
    }
}
