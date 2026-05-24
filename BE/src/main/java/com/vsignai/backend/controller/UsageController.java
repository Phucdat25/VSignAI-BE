package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.UsageTodayResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.security.UserPrincipal;
import com.vsignai.backend.service.UsageService;
import com.vsignai.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageService;
    private final UserService userService;

    @GetMapping("/today")
    public UsageTodayResponse getTodayUsage(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        User user = principal.getUser();

        return usageService.getTodayUsage(user);
    }
}