package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CloudVaultException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message, Object details) {
        super(message, HttpStatus.FORBIDDEN, details);
    }
}
