package com.vsignai.backend.controller;

import com.vsignai.backend.dto.LoginRequest;
import com.vsignai.backend.dto.RegisterRequest;
import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.service.AuthService;
import com.vsignai.backend.util.ResponseFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {

        String result = authService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        return ResponseFactory.success(result, "Register successful");
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseFactory.success(token, "Login successful");
    }

    @PostMapping("/google")
    public ApiResponse<String> loginGoogle(@RequestBody Map<String, String> request) {

        String token = authService.loginWithGoogle(request.get("idToken"));

        return ResponseFactory.success(token, "Login with Google successful");
    }
}