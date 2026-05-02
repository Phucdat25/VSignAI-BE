package com.vsignai.backend.service;

public interface AuthService {
    public String register(String email, String password);
    public String login(String email, String password);
}
