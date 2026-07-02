package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CloudVaultException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Object details) {
        super(message, HttpStatus.UNAUTHORIZED, details);
    }
}
