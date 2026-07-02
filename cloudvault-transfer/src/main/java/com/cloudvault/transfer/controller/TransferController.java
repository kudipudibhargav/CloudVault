package com.cloudvault.transfer.controller;

import com.cloudvault.common.response.ApiResponse;
import com.cloudvault.transfer.dto.FileResponse;
import com.cloudvault.transfer.dto.UploadInitRequest;
import com.cloudvault.transfer.dto.UploadInitResponse;
import com.cloudvault.transfer.service.TransferService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/upload/init")
    public ResponseEntity<ApiResponse<UploadInitResponse>> initiateUpload(@Valid @RequestBody UploadInitRequest request,
                                                                          @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        UploadInitResponse response = transferService.initiateUpload(request, token);
        return new ResponseEntity<>(ApiResponse.success(response, "Upload session initialized successfully"), HttpStatus.OK);
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity<ApiResponse<Void>> uploadChunk(@RequestParam("uploadId") String uploadId,
                                                         @RequestParam("chunkIndex") int chunkIndex,
                                                         @RequestParam("file") MultipartFile file) throws IOException {
        transferService.uploadChunk(uploadId, chunkIndex, file.getInputStream(), file.getSize());
        return ResponseEntity.ok(ApiResponse.success(null, "Chunk uploaded successfully"));
    }

    @PostMapping("/upload/complete")
    public ResponseEntity<ApiResponse<FileResponse>> completeUpload(@RequestParam("uploadId") String uploadId) {
        FileResponse response = transferService.completeUpload(uploadId);
        return ResponseEntity.ok(ApiResponse.success(response, "File upload complete and assembled successfully"));
    }

    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable("fileId") UUID fileId,
                             @RequestHeader(value = "Range", required = false) String rangeHeader,
                             @RequestHeader("Authorization") String authHeader,
                             HttpServletResponse response) {
        String token = extractToken(authHeader);
        transferService.downloadFile(fileId, rangeHeader, token, response);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
