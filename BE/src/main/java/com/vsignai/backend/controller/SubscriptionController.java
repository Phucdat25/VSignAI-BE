package com.vsignai.backend.controller;

import com.vsignai.backend.dto.*;
import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.util.ResponseFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.vsignai.backend.security.UserPrincipal;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ApiResponse<List<SubscriptionResponse>> getAll(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        List<SubscriptionResponse> list = subscriptionService.getAll(user.getId());
        return ResponseFactory.success(list, "Subscriptions fetched successfully");
    }

    @GetMapping("/current")
    public ApiResponse<SubscriptionResponse> getCurrent(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        SubscriptionResponse response = subscriptionService.getCurrent(user.getId());
        return ResponseFactory.success(response, "Current subscription fetched");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<SubscriptionResponse> create(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreateSubscriptionRequest request
    ) {
        SubscriptionResponse response =
                subscriptionService.create(user.getId(), request.getPlanId());

        return ResponseFactory.success(response, "Subscription created successfully");
    }

    @PatchMapping("/{subscriptionId}/cancel")
    public ApiResponse<SubscriptionResponse> cancel(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long subscriptionId
    ) {

        SubscriptionResponse response =
                subscriptionService.cancel(user.getId(), subscriptionId);

        return ResponseFactory.success(
                response,
                "Subscription canceled successfully"
        );
    }
}