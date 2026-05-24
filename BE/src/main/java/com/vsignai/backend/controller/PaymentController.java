package com.vsignai.backend.controller;

import com.vsignai.backend.dto.CreatePaymentRequest;
import com.vsignai.backend.dto.PaymentResponse;
import com.vsignai.backend.dto.PaymentStatusResponse;
import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.service.PaymentService;
import com.vsignai.backend.util.ResponseFactory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.vsignai.backend.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subscriptions/{subscriptionId}/payments")
    public ApiResponse<PaymentResponse> create(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long subscriptionId,
            @RequestBody @Valid CreatePaymentRequest request
    ) {
        PaymentResponse response =
                paymentService.create(user.getId(), subscriptionId, request);

        return ResponseFactory.success(response, "Payment created successfully");
    }

    @GetMapping("/subscriptions/{subscriptionId}/payments")
    public ApiResponse<List<PaymentResponse>> getBySubscription(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long subscriptionId
    ) {

        List<PaymentResponse> list =
                paymentService.getBySubscription(
                        user.getId(),
                        subscriptionId
                );

        return ResponseFactory.success(
                list,
                "Payments fetched successfully"
        );
    }

    @GetMapping("/{txnRef}/status")
    public ResponseEntity<ApiResponse<PaymentStatusResponse>> getPaymentStatus(
            @PathVariable String txnRef
    ) {

        return ResponseEntity.ok(
                ResponseFactory.success(
                        paymentService.getPaymentStatus(txnRef),
                        "Payment status fetched successfully"
                )
        );
    }


}