package com.cloudvault.transfer.client;

import com.cloudvault.common.response.ApiResponse;
import com.cloudvault.common.exception.ServiceUnavailableException;
import com.cloudvault.transfer.dto.FileResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CoreClient {

    private final RestTemplate restTemplate;
    private final String coreServiceUrl = "http://localhost:8082";

    public CoreClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "coreServiceCB", fallbackMethod = "checkDeduplicationFallback")
    public FileResponse checkDeduplication(String sha256, long size, String token) {
        String url = UriComponentsBuilder.fromHttpUrl(coreServiceUrl + "/api/v1/files/dedup")
                .queryParam("sha256", sha256)
                .queryParam("size", size)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<FileResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<FileResponse>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getData();
        }
        return null;
    }

    @CircuitBreaker(name = "coreServiceCB", fallbackMethod = "registerFileMetadataFallback")
    public FileResponse registerFileMetadata(String fileName, UUID folderId, UUID workspaceId,
                                             String mimeType, long size, String checksumSha256, String token) {
        String url = coreServiceUrl + "/api/v1/files/metadata";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("name", fileName);
        body.put("folderId", folderId);
        body.put("workspaceId", workspaceId);
        body.put("mimeType", mimeType);
        body.put("size", size);
        body.put("checksumSha256", checksumSha256);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse<FileResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<FileResponse>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getData();
        }

        throw new RuntimeException("Failed to register file metadata in core service: "
                + (response.getBody() != null ? response.getBody().getMessage() : "Unknown error"));
    }

    // Resilience4j Fallback methods
    public FileResponse checkDeduplicationFallback(String sha256, long size, String token, Throwable throwable) {
        System.err.println("Circuit Breaker fallback active for checkDeduplication. Reason: " + throwable.getMessage());
        return null; // Bypasses deduplication checks so upload can proceed directly
    }

    public FileResponse registerFileMetadataFallback(String fileName, UUID folderId, UUID workspaceId,
                                                     String mimeType, long size, String checksumSha256, String token, Throwable throwable) {
        System.err.println("Circuit Breaker fallback active for registerFileMetadata. Reason: " + throwable.getMessage());
        throw new ServiceUnavailableException("Core Metadata Service is down. Unable to upload/register metadata for: " + fileName);
    }
}
