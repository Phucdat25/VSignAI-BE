package com.vsignai.backend.controller;

import com.vsignai.backend.dto.CreateSubscriptionRequest;
import com.vsignai.backend.dto.response.CreateSubscriptionResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<CreateSubscriptionResponse>
    createSubscription(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateSubscriptionRequest request,
            @RequestHeader(
                    value = "Idempotency-Key",
                    required = false
            ) String idempotencyKey,
            HttpServletRequest httpRequest
    ) {

        CreateSubscriptionResponse response =
                subscriptionService.createSubscription(
                        user,
                        request,
                        idempotencyKey,
                        httpRequest
                );

        return ResponseEntity.ok(response);
    }
}