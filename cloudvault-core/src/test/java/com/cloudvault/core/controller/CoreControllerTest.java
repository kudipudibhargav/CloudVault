package com.cloudvault.core.controller;

import com.cloudvault.core.dto.FolderCreateRequest;
import com.cloudvault.core.dto.WorkspaceMemberRequest;
import com.cloudvault.core.dto.WorkspaceRequest;
import com.cloudvault.core.model.File;
import com.cloudvault.core.model.Folder;
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

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class CoreControllerTest {

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
                "user@example.com",
                "ROLE_USER"
        );

        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testWorkspaceLifecycle_Success() throws Exception {
        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("Engineering Devs");
        request.setStorageQuota(5000L); // 5000 bytes

        mockMvc.perform(post("/api/v1/workspaces")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Engineering Devs")))
                .andExpect(jsonPath("$.data.storageQuota", is(5000)))
                .andExpect(jsonPath("$.data.ownerId", is(testUserPrincipal.getId().toString())));
    }

    @Test
    public void testFolderTreeAndMaterializedPaths() throws Exception {
        // Create Workspace
        Workspace workspace = Workspace.builder()
                .name("Shared Space")
                .ownerId(testUserPrincipal.getId())
                .build();
        workspace = workspaceRepository.save(workspace);

        WorkspaceRequest wsRequest = new WorkspaceRequest();
        wsRequest.setName(workspace.getName());

        // Create Member
        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        // Create Root Folder
        FolderCreateRequest rootRequest = new FolderCreateRequest();
        rootRequest.setName("RootFolder");
        rootRequest.setWorkspaceId(workspace.getId());

        MvcResult resultRoot = mockMvc.perform(post("/api/v1/folders")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rootRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.pathMaterialized", is("/")))
                .andReturn();

        String rootFolderIdStr = objectMapper.readTree(resultRoot.getResponse().getContentAsString())
                .get("data").get("id").asText();
        UUID rootFolderId = UUID.fromString(rootFolderIdStr);

        // Create Subfolder
        FolderCreateRequest subRequest = new FolderCreateRequest();
        subRequest.setName("SubFolder");
        subRequest.setParentId(rootFolderId);
        subRequest.setWorkspaceId(workspace.getId());

        mockMvc.perform(post("/api/v1/folders")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.pathMaterialized", is("/" + rootFolderIdStr + "/")));
    }

    @Test
    public void testFolderMove_CircularCheck() throws Exception {
        Workspace workspace = workspaceRepository.save(Workspace.builder()
                .name("Move Space")
                .ownerId(testUserPrincipal.getId())
                .build());
        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        // Create parent folder A
        Folder parentFolder = folderRepository.save(Folder.builder()
                .name("FolderA")
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .pathMaterialized("/")
                .build());

        // Create child folder B under A
        Folder childFolder = folderRepository.save(Folder.builder()
                .name("FolderB")
                .parent(parentFolder)
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .pathMaterialized("/" + parentFolder.getId() + "/")
                .build());

        // Attempting to move A inside B (circular reference) should fail
        mockMvc.perform(patch("/api/v1/folders/" + parentFolder.getId() + "/move")
                        .principal(auth)
                        .param("targetParentId", childFolder.getId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot move a folder into itself")));
    }

    @Test
    public void testFolderSoftDeletionCascades() throws Exception {
        Workspace workspace = workspaceRepository.save(Workspace.builder()
                .name("Delete Space")
                .ownerId(testUserPrincipal.getId())
                .build());
        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        Folder folderA = folderRepository.save(Folder.builder()
                .name("FolderA")
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .pathMaterialized("/")
                .build());

        Folder folderB = folderRepository.save(Folder.builder()
                .name("FolderB")
                .parent(folderA)
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .pathMaterialized("/" + folderA.getId() + "/")
                .build());

        File fileInB = fileRepository.save(File.builder()
                .name("Doc.pdf")
                .folder(folderB)
                .workspace(workspace)
                .creatorId(testUserPrincipal.getId())
                .mimeType("application/pdf")
                .size(100L)
                .build());

        // Delete parent folder A
        mockMvc.perform(delete("/api/v1/folders/" + folderA.getId())
                        .principal(auth))
                .andExpect(status().isOk());

        // Verify folder A, folder B and fileInB are all soft deleted
        assertTrue(folderRepository.findById(folderA.getId()).get().isDeleted());
        assertTrue(folderRepository.findById(folderB.getId()).get().isDeleted());
        assertTrue(fileRepository.findById(fileInB.getId()).get().isDeleted());
    }

    @Test
    public void testFileRegistryStorageQuotas() throws Exception {
        Workspace workspace = workspaceRepository.save(Workspace.builder()
                .name("Quota Space")
                .ownerId(testUserPrincipal.getId())
                .storageQuota(1000L) // 1000 bytes max
                .storageUsed(0L)
                .build());
        memberRepository.save(com.cloudvault.core.model.WorkspaceMember.builder()
                .workspace(workspace)
                .userId(testUserPrincipal.getId())
                .role(WorkspaceRole.OWNER)
                .build());

        com.cloudvault.core.dto.FileMetadataRequest req = new com.cloudvault.core.dto.FileMetadataRequest();
        req.setName("image.png");
        req.setWorkspaceId(workspace.getId());
        req.setMimeType("image/png");
        req.setSize(400L); // Within limits

        // Creation succeeds
        mockMvc.perform(post("/api/v1/files/metadata")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.size", is(400)));

        // Consumed capacity should now be 400
        assertEquals(400L, workspaceRepository.findById(workspace.getId()).get().getStorageUsed());

        // Registering a file of 700 bytes exceeds remaining 600 quota space
        com.cloudvault.core.dto.FileMetadataRequest overflowReq = new com.cloudvault.core.dto.FileMetadataRequest();
        overflowReq.setName("large.zip");
        overflowReq.setWorkspaceId(workspace.getId());
        overflowReq.setMimeType("application/zip");
        overflowReq.setSize(700L);

        mockMvc.perform(post("/api/v1/files/metadata")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overflowReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Insufficient storage space")));
    }
}
