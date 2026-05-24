package com.vsignai.backend.exceptions;

import com.vsignai.backend.config.RequestContext;
import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.util.ExceptionResponseFactory;

import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.HttpRequestMethodNotSupportedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== APP EXCEPTION (BUSINESS) =====
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleAppException(AppException ex) {

        log.warn(
                "[LAYER: {}] AppException | RequestID: {} | Status: {} | Message: {}",
                ex.getLayer(),
                ex.getRequestId(),
                ex.getStatusCode(),
                ex.getMessage()
        );

        return ResponseEntity.status(ex.getStatusCode())
                .body(
                        ExceptionResponseFactory.error(
                                ex.getMessage(),
                                ex.getErrorCode(),
                                ex.getRequestId()
                        )
                );
    }

    // ===== VALIDATION =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleValidationExceptions(MethodArgumentNotValidException ex) {

        String requestId = getRequestId();

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn(
                "[LAYER: CONTROLLER] Validation Failed | RequestID: {} | Errors: {}",
                requestId,
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponseFactory.error(
                                "Validation Failed",
                                errors,
                                requestId
                        )
                );
    }

    // ===== CONSTRAINT VALIDATION =====
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleConstraintViolationException(
            ConstraintViolationException ex
    ) {

        String requestId = getRequestId();

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations()
                .forEach(error ->
                        errors.put(
                                error.getPropertyPath().toString(),
                                error.getMessage()
                        )
                );

        log.warn(
                "[LAYER: CONTROLLER] Constraint Violation | RequestID: {} | Errors: {}",
                requestId,
                errors
        );

        return ResponseEntity.badRequest()
                .body(
                        ExceptionResponseFactory.error(
                                "Validation Failed",
                                errors,
                                requestId
                        )
                );
    }

    // ===== INVALID ARGUMENT =====
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: SERVICE] Invalid Argument | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage()
        );

        return ResponseEntity.badRequest()
                .body(
                        ExceptionResponseFactory.error(
                                ex.getMessage(),
                                requestId
                        )
                );
    }

    // ===== ACCESS DENIED =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleAccessDeniedException(
            AccessDeniedException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: SECURITY] Access Denied | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ExceptionResponseFactory.error(
                                "You do not have permission",
                                requestId
                        )
                );
    }

    // ===== AUTHENTICATION =====
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleAuthenticationException(
            AuthenticationException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: SECURITY] Authentication Failed | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponseFactory.error(
                                "Unauthenticated",
                                requestId
                        )
                );
    }

    // ===== METHOD NOT ALLOWED =====
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: CONTROLLER] Method Not Allowed | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        ExceptionResponseFactory.error(
                                "Method Not Allowed",
                                requestId
                        )
                );
    }

    // ===== FILE TOO LARGE =====
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: CONTROLLER] File Size Exceeded | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(
                        ExceptionResponseFactory.error(
                                "File too large",
                                requestId
                        )
                );
    }

    // ===== RESOURCE NOT FOUND =====
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleNoHandlerFoundException(
            NoHandlerFoundException ex
    ) {

        String requestId = getRequestId();

        log.warn(
                "[LAYER: CONTROLLER] Resource Not Found | RequestID: {} | URI: {}",
                requestId,
                ex.getRequestURL()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ExceptionResponseFactory.error(
                                "Resource Not Found",
                                requestId
                        )
                );
    }

    // ===== FALLBACK =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>>
    handleAllExceptions(Exception ex) {

        String requestId = getRequestId();

        log.error(
                "[LAYER: UNKNOWN] Unexpected Exception | RequestID: {} | Error: {}",
                requestId,
                ex.getMessage(),
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponseFactory.error(
                                "Internal Server Error",
                                requestId
                        )
                );
    }

    private String getRequestId() {

        return RequestContext.getRequestId() != null
                ? RequestContext.getRequestId()
                : "UNKNOWN";
    }
}
