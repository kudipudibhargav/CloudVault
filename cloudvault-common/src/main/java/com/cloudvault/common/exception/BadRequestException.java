package com.cloudvault.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CloudVaultException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Object details) {
        super(message, HttpStatus.BAD_REQUEST, details);
    }
}
