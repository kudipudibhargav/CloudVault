package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class CloudVaultException extends RuntimeException {
    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final Object details;

    public CloudVaultException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.details = null;
    }

    public CloudVaultException(String message, HttpStatus status, Object details) {
        super(message);
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getDetails() {
        return details;
    }
}
