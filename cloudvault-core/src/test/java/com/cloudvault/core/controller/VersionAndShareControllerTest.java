package com.cloudvault.core.controller;

import com.cloudvault.core.dto.ShareLinkRequest;
import com.cloudvault.core.model.File;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class VersionAndShareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository memberRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileVersionRepository versionRepository;

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        shareLinkRepository.deleteAll();
        versionRepository.deleteAll();
        commentRepository.deleteAll();
        fileRepository.deleteAll();
        folderRepository.deleteAll();
        memberRepository.deleteAll();
        workspaceRepository.deleteAll();

        testUserPrincipal = UserPrincipal.create(
                UUID.randomUUID(),
                "owner@example.com",
                "ROLE_USER"
        );

        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Provision base Workspace and File
        workspace = workspaceRepository.save(Workspace.builder()
                .name("Versioning Space")
                .ownerId(testUserPrincipal.getId())
                .storageQuota(10000L)
                .storageUsed(0L)
                .build());

        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        file = fileRepository.save(File.builder()
                .name("document.docx")
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .mimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .size(100L)
                .build());
    }

    @Test
    public void testFileVersionControlAndRollback() throws Exception {
        String fileId = file.getId().toString();

        // 1. Create Version 1 (Size 100, hash1)
        mockMvc.perform(post("/api/v1/files/" + fileId + "/versions")
                        .principal(auth)
                        .param("versionHash", "sha256-hash1")
                        .param("size", "100"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.versionNumber", is(1)))
                .andExpect(jsonPath("$.data.versionHash", is("sha256-hash1")));

        // 2. Create Version 2 (Size 150, hash2)
        mockMvc.perform(post("/api/v1/files/" + fileId + "/versions")
                        .principal(auth)
                        .param("versionHash", "sha256-hash2")
                        .param("size", "150"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.versionNumber", is(2)))
                .andExpect(jsonPath("$.data.versionHash", is("sha256-hash2")));

        // Verify file details reflect latest v2
        File updatedFile = fileRepository.findById(file.getId()).get();
        assertEquals("sha256-hash2", updatedFile.getChecksumSha256());
        assertEquals(150L, updatedFile.getSize());
        assertEquals("2", updatedFile.getCurrentVersionId());

        // 3. Rollback to version v1
        mockMvc.perform(post("/api/v1/files/" + fileId + "/versions/1/rollback")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.versionNumber", is(1)))
                .andExpect(jsonPath("$.data.versionHash", is("sha256-hash1")));

        // Verify main file reverted to v1 details
        File rolledFile = fileRepository.findById(file.getId()).get();
        assertEquals("sha256-hash1", rolledFile.getChecksumSha256());
        assertEquals(100L, rolledFile.getSize());
        assertEquals("1", rolledFile.getCurrentVersionId());

        // 4. Retrieve Version History List
        mockMvc.perform(get("/api/v1/files/" + fileId + "/versions")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].versionNumber", is(2)))
                .andExpect(jsonPath("$.data[1].versionNumber", is(1)));
    }

    @Test
    public void testShareLinkLifecycle() throws Exception {
        ShareLinkRequest req = new ShareLinkRequest();
        req.setFileId(file.getId());
        req.setPassword("secretPasscode");
        req.setExpiresAtDays(1);
        req.setDownloadLimit(1); // Link valid for single download only
        req.setPermissionLevel("VIEWER");

        // 1. Create Share Link
        MvcResult res = mockMvc.perform(post("/api/v1/shares")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.passwordProtected", is(true)))
                .andReturn();

        String token = objectMapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("accessToken").asText();

        // 2. Resolve link without passcode -> Should Fail (403)
        mockMvc.perform(post("/api/v1/shares/" + token + "/resolve"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Invalid share passcode")));

        // 3. Resolve link with wrong passcode -> Should Fail (403)
        mockMvc.perform(post("/api/v1/shares/" + token + "/resolve")
                        .param("password", "wrong-code"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Invalid share passcode")));

        // 4. Resolve link with correct passcode -> Succeeds (200)
        mockMvc.perform(post("/api/v1/shares/" + token + "/resolve")
                        .param("password", "secretPasscode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.downloadCount", is(1)));

        // 5. Resolve link again -> Fails (403) because downloadLimit (1) exceeded
        mockMvc.perform(post("/api/v1/shares/" + token + "/resolve")
                        .param("password", "secretPasscode"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Download limit exceeded")));
    }
}
