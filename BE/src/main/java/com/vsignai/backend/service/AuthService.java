package com.vsignai.backend.service;

import com.vsignai.backend.dto.response.AuthResponse;
import com.vsignai.backend.entity.User;

public interface AuthService {
    public AuthResponse register(String email, String password, String name);
    public AuthResponse login(String email, String password);
    public AuthResponse loginWithGoogle(String idToken);
}
