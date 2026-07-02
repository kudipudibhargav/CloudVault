package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends CloudVaultException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Object details) {
        super(message, HttpStatus.CONFLICT, details);
    }
}
