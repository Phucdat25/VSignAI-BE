package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.AiPredictResponse;
import com.vsignai.backend.dto.response.ApiResponse;
import com.vsignai.backend.dto.response.UsageTodayResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.service.AiService;
import com.vsignai.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final UsageService usageService;

    @PostMapping("/predict")
    public ApiResponse<AiPredictResponse> predict(
            @RequestParam("file") MultipartFile file,
            @RequestParam("durationSeconds") Integer durationSeconds
    ) {
        AiPredictResponse response = aiService.predict(file, durationSeconds);
        return ApiResponse.success("Predict successfully", response);
    }

    @GetMapping("/usage/today")
    public ApiResponse<UsageTodayResponse> getTodayUsage() {
        User user = getCurrentUser();
        UsageTodayResponse response = usageService.getTodayUsage(user);
        return ApiResponse.success(response);
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new AppException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
    }
}
