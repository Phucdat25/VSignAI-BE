package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.AuthResponse;
import com.vsignai.backend.dto.LoginRequest;
import com.vsignai.backend.dto.RegisterRequest;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request.getEmail(), request.getPassword(), request.getName());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/google")
    public AuthResponse loginGoogle(@RequestBody Map<String, String> request) {
        return authService.loginWithGoogle(request.get("idToken"));
    }


}
