package com.vsignai.backend.util;

import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.config.RequestContext;
import com.vsignai.backend.dto.common.PaginationResponseDTO;

import java.time.Instant;

public final class ResponseFactory {

    private ResponseFactory() {
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .requestId(RequestContext.getRequestId())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> error(
            String message,
            String errorCode
    ) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .requestId(RequestContext.getRequestId())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<PaginationResponseDTO<T>> successPage(
            PaginationResponseDTO<T> data,
            String message
    ) {
        return ApiResponse.<PaginationResponseDTO<T>>builder()
                .success(true)
                .message(message)
                .data(data)
                .requestId(RequestContext.getRequestId())
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> successMessage(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .requestId(RequestContext.getRequestId())
                .timestamp(Instant.now())
                .build();
    }
}