package com.cloudvault.common.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Object errors;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request processed successfully");
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();
        private Object errors;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder<T> errors(Object errors) {
            this.errors = errors;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, data, timestamp, errors);
        }
    }
}
