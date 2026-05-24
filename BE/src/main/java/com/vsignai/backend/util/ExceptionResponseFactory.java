package com.vsignai.backend.util;

import com.vsignai.backend.dto.common.ApiResponse;

import java.time.Instant;
import java.time.LocalDateTime;

public class ExceptionResponseFactory {

    public static ApiResponse<Object> error(
            String message,
            String requestId
    ) {

        return ApiResponse.builder()
                .success(false)
                .message(message)
                .requestId(requestId)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Object> error(
            String message,
            Object data,
            String requestId
    ) {

        return ApiResponse.builder()
                .success(false)
                .message(message)
                .data(data)
                .requestId(requestId)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Object> error(
            String message,
            String errorCode,
            String requestId
    ) {

        return ApiResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .requestId(requestId)
                .timestamp(Instant.now())
                .build();
    }
}