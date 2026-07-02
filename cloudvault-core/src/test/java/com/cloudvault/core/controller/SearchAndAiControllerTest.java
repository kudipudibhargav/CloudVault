package com.cloudvault.core.controller;

import com.cloudvault.core.dto.FileMetadataRequest;
import com.cloudvault.core.model.Workspace;
import com.cloudvault.core.model.WorkspaceRole;
import com.cloudvault.core.repository.*;
import com.cloudvault.core.security.UserPrincipal;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class SearchAndAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository memberRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileVersionRepository versionRepository;

    @Autowired
    private ShareLinkRepository shareLinkRepository;

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
        shareLinkRepository.deleteAll();
        versionRepository.deleteAll();
        commentRepository.deleteAll();
        fileRepository.deleteAll();
        folderRepository.deleteAll();
        memberRepository.deleteAll();
        workspaceRepository.deleteAll();

        testUserPrincipal = UserPrincipal.create(
                UUID.randomUUID(),
                "searcher@example.com",
                "ROLE_USER"
        );

        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Provision base Workspace
        workspace = workspaceRepository.save(Workspace.builder()
                .name("Search Space")
                .ownerId(testUserPrincipal.getId())
                .storageQuota(100000L)
                .storageUsed(0L)
                .build());

        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());
    }

    @Test
    public void testAiAutoTaggingAndMultiFacetedSearch() throws Exception {
        String workspaceId = workspace.getId().toString();

        // 1. Create PDF file metadata -> triggers PDF AI tags and summary
        FileMetadataRequest pdfReq = new FileMetadataRequest();
        pdfReq.setName("migration_plan.pdf");
        pdfReq.setWorkspaceId(workspace.getId());
        pdfReq.setMimeType("application/pdf");
        pdfReq.setSize(1000L);
        pdfReq.setChecksumSha256("pdf-hash-111");

        mockMvc.perform(post("/api/v1/files/metadata")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pdfReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tags", hasItems("pdf", "document", "report")))
                .andExpect(jsonPath("$.data.aiSummary", containsString("cloud migration strategies")));

        // 2. Create Image file metadata -> triggers Image AI tags and summary
        FileMetadataRequest imgReq = new FileMetadataRequest();
        imgReq.setName("invoice_receipt.png");
        imgReq.setWorkspaceId(workspace.getId());
        imgReq.setMimeType("image/png");
        imgReq.setSize(500L);
        imgReq.setChecksumSha256("img-hash-222");

        mockMvc.perform(post("/api/v1/files/metadata")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(imgReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tags", hasItems("image", "document", "invoice", "receipt")))
                .andExpect(jsonPath("$.data.aiSummary", containsString("tax listings")));

        // 3. Search query: "migration" -> Matches PDF only
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/search")
                        .principal(auth)
                        .param("q", "migration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("migration_plan.pdf")));

        // 4. Search query: "invoice" tag -> Matches Image only
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/search")
                        .principal(auth)
                        .param("tag", "invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("invoice_receipt.png")));

        // 5. Search query: filter by mimeType "application/pdf" -> Matches PDF only
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/search")
                        .principal(auth)
                        .param("mimeType", "application/pdf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("migration_plan.pdf")));
    }
}
