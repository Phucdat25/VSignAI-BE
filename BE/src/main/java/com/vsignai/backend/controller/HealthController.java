package com.vsignai.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "VSignAI Backend is running";
    }

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}