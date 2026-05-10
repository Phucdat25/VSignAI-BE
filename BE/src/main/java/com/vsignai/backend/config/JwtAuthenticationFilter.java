package com.vsignai.backend.config;

import com.vsignai.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy chuỗi "Authorization" từ Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Kiểm tra xem có Token không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Tách Token ra khỏi chữ "Bearer "
        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractEmail(jwt); // Dùng hàm bạn đã viết

            // 4. Nếu giải mã được email và chưa được xác thực trong Context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, null, new ArrayList<>() // Ở đây có thể thêm danh sách quyền (Roles)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đánh dấu người dùng đã đăng nhập hợp lệ
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Token sai hoặc hết hạn
            System.out.println("Lỗi Token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}