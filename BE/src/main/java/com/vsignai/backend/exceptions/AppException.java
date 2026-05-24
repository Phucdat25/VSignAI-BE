package com.vsignai.backend.exceptions;

import com.vsignai.backend.config.RequestContext;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class AppException extends RuntimeException {

    private final String errorCode;

    private final HttpStatus statusCode;

    private final String requestId;

    private final String layer;

    private final LocalDateTime timestamp;

    public AppException(
            String errorCode,
            String message,
            HttpStatus statusCode
    ) {

        super(message);

        this.errorCode = errorCode;
        this.statusCode = statusCode;

        this.requestId =
                RequestContext.getRequestId() != null
                        ? RequestContext.getRequestId()
                        : "UNKNOWN";

        this.layer =
                RequestContext.getCurrentLayer() != null
                        ? RequestContext.getCurrentLayer()
                        : "UNKNOWN";

        this.timestamp = LocalDateTime.now();
    }
}