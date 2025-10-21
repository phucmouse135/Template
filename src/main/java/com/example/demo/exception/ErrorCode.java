package com.example.demo.exception;

public enum ErrorCode {
    INVALID_REQUEST(1000, "Invalid request parameters", 400),
    RESOURCE_NOT_FOUND(1001, "Requested resource not found", 404),
    INTERNAL_SERVER_ERROR(1002, "Internal server error occurred", 500);

    private final int code;
    private final String message;
    private final int httpStatus;

    ErrorCode(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
