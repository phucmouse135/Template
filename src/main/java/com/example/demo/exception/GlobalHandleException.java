package com.example.demo.exception;

import com.example.demo.model.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(value = Exception.class)
    public ErrorCode handleException(Exception e) {
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    @ExceptionHandler(AppException.class)
    public ApiResponse<?> handleAppException(AppException e) {
        return ApiResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .data(e.getMessage())
                .build();
    }
}
