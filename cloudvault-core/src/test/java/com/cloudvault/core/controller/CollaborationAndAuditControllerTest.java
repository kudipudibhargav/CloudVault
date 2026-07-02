package com.cloudvault.core.controller;

import com.cloudvault.core.dto.CommentRequest;
import com.cloudvault.core.dto.WorkspaceRequest;
import com.cloudvault.core.model.AuditLog;
import com.cloudvault.core.model.Comment;
import com.cloudvault.core.model.File;
import com.cloudvault.core.model.Workspace;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.*;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.websocket.CollaborationWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class CollaborationAndAuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository memberRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CollaborationWebSocketHandler webSocketHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    private UserPrincipal testUserPrincipal;
    private UsernamePasswordAuthenticationToken auth;
    private Workspace workspace;
    private File file;

    @BeforeEach
    public void setup() {
        commentRepository.deleteAll();
        auditLogRepository.deleteAll();
        fileRepository.deleteAll();
        memberRepository.deleteAll();
        workspaceRepository.deleteAll();

        testUserPrincipal = UserPrincipal.create(
                UUID.randomUUID(),
                "collab.user@example.com",
                "ROLE_USER"
        );

        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Provision base entities
        workspace = workspaceRepository.save(Workspace.builder()
                .name("Collaboration Space")
                .ownerId(testUserPrincipal.getId())
                .build());

        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        file = fileRepository.save(File.builder()
                .name("notes.txt")
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .mimeType("text/plain")
                .size(50L)
                .build());
    }

    @Test
    public void testAopAuditLogging() throws Exception {
        // Assert no audit logs exist initially
        assertEquals(0, auditLogRepository.count());

        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("Logged Workspace");
        request.setStorageQuota(3000L);

        // Mutating call matching @LogAction("WORKSPACE_CREATE")
        mockMvc.perform(post("/api/v1/workspaces")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Assert audit log record was successfully recorded via AOP aspect
        List<AuditLog> logs = auditLogRepository.findAll();
        assertEquals(1, logs.size());
        assertEquals("WORKSPACE_CREATE", logs.get(0).getAction());
        assertEquals("WORKSPACE", logs.get(0).getEntityType());
        assertNotNull(logs.get(0).getEntityId());
        assertTrue(logs.get(0).getDetails().contains("Logged Workspace"));
    }

    @Test
    public void testCommentSystemAndMentions() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setContent("Please review this file @admin and @manager_user");

        // Add Comment
        mockMvc.perform(post("/api/v1/files/" + file.getId() + "/comments")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content", is("Please review this file @admin and @manager_user")))
                .andExpect(jsonPath("$.data.authorName", is(testUserPrincipal.getEmail())));

        // Fetch Comments
        mockMvc.perform(get("/api/v1/files/" + file.getId() + "/comments")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].content", containsString("@admin")));
    }

    @Test
    public void testWebSocketMessagingStateFlow() throws Exception {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.getId()).thenReturn("mock-sess-100");
        when(mockSession.isOpen()).thenReturn(true);

        // 1. Send SUBSCRIBE message
        String subscribeJson = "{\"type\":\"SUBSCRIBE\",\"workspaceId\":\"" + workspace.getId().toString() + "\"}";
        webSocketHandler.handleMessage(mockSession, new TextMessage(subscribeJson));

        // Verify acknowledgment sent back to client
        verify(mockSession, times(1)).sendMessage(org.mockito.ArgumentMatchers.any(TextMessage.class));

        // 2. Send EDIT message
        String editJson = "{\"type\":\"EDIT\",\"workspaceId\":\"" + workspace.getId().toString() + "\",\"details\":\"renamed folder\"}";
        webSocketHandler.handleMessage(mockSession, new TextMessage(editJson));

        // Close connection
        webSocketHandler.afterConnectionClosed(mockSession, org.springframework.web.socket.CloseStatus.NORMAL);
    }
}
