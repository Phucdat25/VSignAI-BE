package com.vsignai.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RequestTracingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.profiles.active:dev}")
    private String profile;

    private static final String LINE = "═══════════════════════════════════════════════════════════════";
    private static final String THIN_LINE = "───────────────────────────────────────────────────────────────";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = Optional.ofNullable(request.getHeader("X-Request-ID"))
                .orElse(UUID.randomUUID().toString());

        long start = System.currentTimeMillis();

        try {
            RequestContext.setRequestId(requestId);
            RequestContext.setRequestUri(request.getRequestURI());
            RequestContext.setRequestMethod(request.getMethod());

            // ===== MDC =====
            MDC.put("requestId", requestId);
            MDC.put("method", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            MDC.put("timestamp", Instant.now().toString());

            // ===== USER ID =====
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !(auth.getPrincipal() instanceof String)) {

                String userId = auth.getName();
                MDC.put("userId", userId);
                RequestContext.setUserId(userId);
            }

            response.setHeader("X-Request-ID", requestId);

            // ===== DEV LOG START =====
            if ("dev".equals(profile)) {
                log.info(
                        "\n{}\n  ► REQUEST START\n{}\n  Request ID : {}\n  Method     : {}\n  URI        : {}\n  Remote IP  : {}\n{}",
                        LINE, THIN_LINE,
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LINE
                );
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - start;

            if ("dev".equals(profile)) {
                log.error(
                        "\n{}\n  ✖ REQUEST FAILED\n{}\n  Request ID : {}\n  Duration   : {} ms\n  Error      : {}\n{}",
                        LINE, THIN_LINE,
                        requestId,
                        duration,
                        e.getMessage(),
                        LINE,
                        e
                );
            } else {
                Map<String, Object> errorLog = new HashMap<>();
                errorLog.put("type", "HTTP_ERROR");
                errorLog.put("requestId", requestId);
                errorLog.put("error", e.getMessage());
                errorLog.put("duration", duration);

                log.error(objectMapper.writeValueAsString(errorLog), e);
            }

            throw e;

        } finally {

            long duration = System.currentTimeMillis() - start;

            if ("dev".equals(profile)) {
                log.info(
                        "\n{}\n  ◄ REQUEST END\n{}\n  Request ID : {}\n  Status     : {}\n  Duration   : {} ms\n{}",
                        LINE, THIN_LINE,
                        requestId,
                        response.getStatus(),
                        duration,
                        LINE
                );
            } else {
                try {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("type", "HTTP_REQUEST");
                    logData.put("requestId", requestId);
                    logData.put("method", request.getMethod());
                    logData.put("uri", request.getRequestURI());
                    logData.put("status", response.getStatus());
                    logData.put("duration", duration);
                    logData.put("userId", MDC.get("userId"));

                    log.info(objectMapper.writeValueAsString(logData));

                } catch (Exception e) {
                    log.error("Failed to write JSON log", e);
                }
            }

            MDC.clear();
            RequestContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/static");
    }
}