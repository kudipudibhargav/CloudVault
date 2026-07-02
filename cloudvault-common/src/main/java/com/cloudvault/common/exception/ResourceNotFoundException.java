package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CloudVaultException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String message, Object details) {
        super(message, HttpStatus.NOT_FOUND, details);
    }
}
