package com.vsignai.backend.controller;

import com.vsignai.backend.dto.SubscriptionPlanRequest;
import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.dto.response.AdminSubscriptionPlanResponse;
import com.vsignai.backend.service.AdminSubscriptionPlanService;
import com.vsignai.backend.util.ResponseFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/subscription-plans")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSubscriptionPlanController {

    private final AdminSubscriptionPlanService adminSubscriptionPlanService;

    @GetMapping
    public ApiResponse<List<AdminSubscriptionPlanResponse>> getAllPlans() {

        return ResponseFactory.success(
                adminSubscriptionPlanService.getAllPlans()
        );
    }

    @PostMapping
    public ApiResponse<AdminSubscriptionPlanResponse> createPlan(
            @RequestBody SubscriptionPlanRequest request
    ) {

        return ResponseFactory.success(
                adminSubscriptionPlanService.createPlan(request),
                "Create subscription plan successfully"
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminSubscriptionPlanResponse> updatePlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanRequest request
    ) {

        return ResponseFactory.success(
                adminSubscriptionPlanService.updatePlan(id, request),
                "Update subscription plan successfully"
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updatePlanStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive
    ) {

        adminSubscriptionPlanService.updatePlanStatus(id, isActive);

        return ResponseFactory.success(
                null,
                "Update plan status successfully"
        );
    }
}