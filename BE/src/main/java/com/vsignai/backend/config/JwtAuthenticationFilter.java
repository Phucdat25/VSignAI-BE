package com.vsignai.backend.config;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.security.UserPrincipal;
import com.vsignai.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        try {

            if (StringUtils.hasText(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🔐 Validate token
                if (!jwtService.isValidToken(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 📩 Extract email
                String email = jwtService.extractEmail(token);

                // 🔍 Load user
                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user != null) {

                    // 👤 Build principal
                    UserPrincipal principal = UserPrincipal.from(user);

                    // 🔐 Create authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // ✅ Set vào SecurityContext
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("JWT authentication failed", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {

            return bearerToken.substring(7);
        }

        return null;
    }
}