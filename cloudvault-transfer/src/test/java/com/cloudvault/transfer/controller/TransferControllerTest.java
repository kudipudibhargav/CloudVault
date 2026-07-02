package com.cloudvault.transfer.controller;

import com.cloudvault.transfer.client.CoreClient;
import com.cloudvault.transfer.dto.FileResponse;
import com.cloudvault.transfer.dto.UploadInitRequest;
import com.cloudvault.transfer.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private CoreClient coreClient;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    private UserPrincipal testUserPrincipal;
    private UsernamePasswordAuthenticationToken auth;
    private String mockToken;

    @BeforeEach
    public void setup() {
        testUserPrincipal = UserPrincipal.create(
                UUID.randomUUID(),
                "uploader@example.com",
                "ROLE_USER"
        );
        auth = new UsernamePasswordAuthenticationToken(
                testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockToken = "mocked-jwt-token";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testUploadInit_Deduplicated() throws Exception {
        // Mock CoreClient returning an existing file for SHA-256
        FileResponse existingFile = new FileResponse();
        existingFile.setId(UUID.randomUUID());
        existingFile.setName("test.pdf");
        existingFile.setSize(100L);

        when(coreClient.checkDeduplication(eq("matched-sha256"), eq(100L), anyString()))
                .thenReturn(existingFile);

        UploadInitRequest request = new UploadInitRequest();
        request.setFileName("test.pdf");
        request.setSize(100L);
        request.setWorkspaceId(UUID.randomUUID());
        request.setSha256("matched-sha256");
        request.setTotalChunks(1);
        request.setMimeType("application/pdf");

        mockMvc.perform(post("/api/v1/transfer/upload/init")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.deduplicated", is(true)))
                .andExpect(jsonPath("$.data.fileId", is(existingFile.getId().toString())))
                .andExpect(jsonPath("$.data.uploadId", nullValue()));
    }

    @Test
    public void testUploadInit_NewUpload() throws Exception {
        UUID newFileId = UUID.randomUUID();
        FileResponse reservedFile = new FileResponse();
        reservedFile.setId(newFileId);
        reservedFile.setName("new.zip");

        when(coreClient.checkDeduplication(eq("new-sha256"), eq(200L), anyString()))
                .thenReturn(null);

        when(coreClient.registerFileMetadata(eq("new.zip"), any(), any(), eq("application/zip"), eq(200L), eq("new-sha256"), anyString()))
                .thenReturn(reservedFile);

        UploadInitRequest request = new UploadInitRequest();
        request.setFileName("new.zip");
        request.setSize(200L);
        request.setWorkspaceId(UUID.randomUUID());
        request.setSha256("new-sha256");
        request.setTotalChunks(2);
        request.setMimeType("application/zip");

        mockMvc.perform(post("/api/v1/transfer/upload/init")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.deduplicated", is(false)))
                .andExpect(jsonPath("$.data.fileId", is(newFileId.toString())))
                .andExpect(jsonPath("$.data.uploadId", notNullValue()));

        verify(valueOperations, times(1)).set(anyString(), anyString(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    public void testUploadChunk_Success() throws Exception {
        String uploadId = "test-upload-id";
        String sessionJson = "{\"uploadId\":\"test-upload-id\",\"fileName\":\"new.zip\",\"size\":200,\"totalChunks\":2,\"fileId\":\"" + UUID.randomUUID().toString() + "\",\"completedChunks\":[]}";

        when(valueOperations.get(anyString())).thenReturn(sessionJson);

        MockMultipartFile filePart = new MockMultipartFile(
                "file", "chunk0", MediaType.APPLICATION_OCTET_STREAM_VALUE, "chunk content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/transfer/upload/chunk")
                        .file(filePart)
                        .param("uploadId", uploadId)
                        .param("chunkIndex", "0")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("uploaded successfully")));

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUploadComplete_Success() throws Exception {
        String uploadId = "test-upload-id";
        // All chunks are completed (totalChunks is 1, completedChunks contains 0)
        String sessionJson = "{\"uploadId\":\"test-upload-id\",\"fileName\":\"new.zip\",\"size\":200,\"totalChunks\":1,\"fileId\":\"" + UUID.randomUUID().toString() + "\",\"completedChunks\":[0]}";

        when(valueOperations.get(anyString())).thenReturn(sessionJson);

        mockMvc.perform(post("/api/v1/transfer/upload/complete")
                        .param("uploadId", uploadId)
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("assembled successfully")));

        verify(minioClient, times(1)).composeObject(any(ComposeObjectArgs.class));
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    public void testDownloadFile_RangeRequest() throws Exception {
        UUID fileId = UUID.randomUUID();
        FileResponse metadata = new FileResponse();
        metadata.setId(fileId);
        metadata.setName("video.mp4");
        metadata.setMimeType("video/mp4");
        metadata.setSize(1000L);

        when(coreClient.checkDeduplication(anyString(), anyLong(), anyString())).thenReturn(metadata);

        StatObjectResponse statResponse = mock(StatObjectResponse.class);
        when(statResponse.size()).thenReturn(1000L);
        when(statResponse.contentType()).thenReturn("video/mp4");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(statResponse);

        GetObjectResponse getResponse = mock(GetObjectResponse.class);
        // Stub reading 50 bytes of range
        when(getResponse.read(any(byte[].class), anyInt(), anyInt())).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            String mockContent = "range video segment bytes data contents";
            byte[] src = mockContent.getBytes();
            System.arraycopy(src, 0, buffer, 0, src.length);
            return src.length;
        });

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getResponse);

        mockMvc.perform(get("/api/v1/transfer/download/" + fileId)
                        .header("Range", "bytes=100-149")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isPartialContent())
                .andExpect(header().string("Content-Range", "bytes 100-149/1000"))
                .andExpect(header().string("Content-Length", "50"));
    }
}
