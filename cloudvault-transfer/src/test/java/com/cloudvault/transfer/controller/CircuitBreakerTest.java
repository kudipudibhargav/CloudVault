package com.cloudvault.transfer.controller;

import com.cloudvault.common.exception.ServiceUnavailableException;
import com.cloudvault.transfer.client.CoreClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local-h2")
public class CircuitBreakerTest {

    @Autowired
    private CoreClient coreClient;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testCircuitBreakerTrippingAndShortCircuit() {
        // Mock restTemplate.exchange to simulate downstream core service failure
        Mockito.when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Core Service Connection Timeout"));

        UUID folderId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();

        // 1. Call 1: should call restTemplate, fail, and throw ServiceUnavailableException
        assertThrows(ServiceUnavailableException.class, () -> {
            coreClient.registerFileMetadata("doc1.txt", folderId, workspaceId, "text/plain", 100L, "hash1", "token");
        });

        // 2. Call 2: should call restTemplate, fail, and throw ServiceUnavailableException
        assertThrows(ServiceUnavailableException.class, () -> {
            coreClient.registerFileMetadata("doc2.txt", folderId, workspaceId, "text/plain", 100L, "hash2", "token");
        });

        // 3. Call 3: Circuit Breaker is now OPEN because minimumNumberOfCalls=2 was reached with 100% failure rate.
        // It should throw ServiceUnavailableException instantly WITHOUT invoking restTemplate!
        assertThrows(ServiceUnavailableException.class, () -> {
            coreClient.registerFileMetadata("doc3.txt", folderId, workspaceId, "text/plain", 100L, "hash3", "token");
        });

        // 4. Verify that restTemplate was only invoked exactly 2 times (not 3 times) because the 3rd was short-circuited!
        verify(restTemplate, times(2)).exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }
}
