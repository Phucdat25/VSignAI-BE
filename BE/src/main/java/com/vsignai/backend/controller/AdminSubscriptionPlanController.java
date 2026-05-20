package com.vsignai.backend.controller;

import com.vsignai.backend.dto.SubscriptionPlanRequest;
import com.vsignai.backend.dto.response.AdminSubscriptionPlanResponse;
import com.vsignai.backend.dto.response.ApiResponse;
import com.vsignai.backend.service.AdminSubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
@RequiredArgsConstructor
public class AdminSubscriptionPlanController {

    private final AdminSubscriptionPlanService adminSubscriptionPlanService;

    @GetMapping
    public ApiResponse<List<AdminSubscriptionPlanResponse>> getAllPlans() {
        return ApiResponse.success(adminSubscriptionPlanService.getAllPlans());
    }

    @PostMapping
    public ApiResponse<AdminSubscriptionPlanResponse> createPlan(
            @RequestBody SubscriptionPlanRequest request
    ) {
        return ApiResponse.success(
                "Create subscription plan successfully",
                adminSubscriptionPlanService.createPlan(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminSubscriptionPlanResponse> updatePlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanRequest request
    ) {
        return ApiResponse.success(
                "Update subscription plan successfully",
                adminSubscriptionPlanService.updatePlan(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updatePlanStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive
    ) {
        adminSubscriptionPlanService.updatePlanStatus(id, isActive);
        return ApiResponse.successMessage("Update plan status successfully");
    }
}
