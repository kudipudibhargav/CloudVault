package com.cloudvault.core.controller;

import com.cloudvault.core.dto.FileMetadataRequest;
import com.cloudvault.core.model.Workspace;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.FileRepository;
import com.cloudvault.core.repository.WorkspaceMemberRepository;
import com.cloudvault.core.repository.WorkspaceRepository;
import com.cloudvault.core.security.UserPrincipal;
import com.cloudvault.core.service.LocalEventPublisher;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class ResilienceAndAmqpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository memberRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private com.cloudvault.core.repository.FolderRepository folderRepository;

    @Autowired
    private LocalEventPublisher localEventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    private UserPrincipal testUserPrincipal;
    private UsernamePasswordAuthenticationToken auth;
    private Workspace workspace;

    @BeforeEach
    public void setup() {
        fileRepository.deleteAll();
        folderRepository.deleteAll();
        memberRepository.deleteAll();
        workspaceRepository.deleteAll();
        localEventPublisher.clear();

        testUserPrincipal = UserPrincipal.create(
                UUID.randomUUID(),
                "resilience@example.com",
                "ROLE_USER"
        );

        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Provision base Workspace with 1000 bytes quota
        workspace = workspaceRepository.save(Workspace.builder()
                .name("Quota Space")
                .ownerId(testUserPrincipal.getId())
                .storageQuota(1000L)
                .storageUsed(0L)
                .build());

        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());
    }

    @Test
    public void testStorageQuotaThresholdAlerts() throws Exception {
        // Register file with 950 bytes (95% of quota!) -> Exceeds 90%
        FileMetadataRequest req = new FileMetadataRequest();
        req.setName("critical_doc.pdf");
        req.setWorkspaceId(workspace.getId());
        req.setMimeType("application/pdf");
        req.setSize(950L);
        req.setChecksumSha256("quota-hash-123");

        mockMvc.perform(post("/api/v1/files/metadata")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // Check if event was published
        List<LocalEventPublisher.PublishedEvent> events = localEventPublisher.getPublishedEvents();
        assertThat(events, hasSize(greaterThanOrEqualTo(1)));
        
        boolean foundQuotaAlert = events.stream()
                .anyMatch(e -> "workspace.quota".equals(e.getRoutingKey()) && 
                               e.getPayload().toString().contains("Storage usage is at 95%"));
                               
        assertThat(foundQuotaAlert, is(true));
    }
}
