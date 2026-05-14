package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.UsageTodayResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageService;
    private final UserRepository userRepository;

    @GetMapping("/today")
    public UsageTodayResponse getTodayUsage(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return usageService.getTodayUsage(user);
    }
}