package com.vsignai.backend.controller;

import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;

    private final PaymentRepository paymentRepository;

    private final SubscriptionService subscriptionService;

    private final ObjectMapper objectMapper;

    @GetMapping("/vnpay-return")
    public String vnpayReturn() {

        return "VNPay payment completed";
    }

    @GetMapping("/vnpay-ipn")
    @Transactional
    public String vnpayIpn(
            @RequestParam Map<String, String> params
    ) {

        // =====================================================
        // STEP 1 — VALIDATE SIGNATURE
        // =====================================================

        boolean validSignature =
                vnPayService.validateSignature(params);

        if (!validSignature) {
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }

        // =====================================================
        // STEP 2 — GET TRANSACTION
        // =====================================================

        String transactionId =
                params.get("vnp_TxnRef");

        Payment payment =
                paymentRepository
                        .findByTransactionId(transactionId)
                        .orElse(null);

        if (payment == null) {
            return "{\"RspCode\":\"01\",\"Message\":\"Payment not found\"}";
        }

        // =====================================================
        // STEP 3 — DUPLICATE CALLBACK CHECK
        // =====================================================

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "{\"RspCode\":\"02\",\"Message\":\"Payment already confirmed\"}";
        }

        // =====================================================
        // STEP 4 — CHECK PAYMENT RESULT
        // =====================================================

        String responseCode =
                params.get("vnp_ResponseCode");

        payment.setGatewayResponseCode(responseCode);

        try {

            payment.setGatewayPayload(
                    objectMapper.writeValueAsString(params)
            );

        } catch (Exception e) {

            payment.setGatewayPayload("{}");
        }

        if ("00".equals(responseCode)) {

            // SUCCESS

            payment.setStatus(PaymentStatus.SUCCESS);

            payment.setPaidAt(LocalDateTime.now());

            subscriptionService.activateSubscription(payment);

        } else {

            // FAILED

            payment.setStatus(PaymentStatus.FAILED);

            payment.setFailureReason(
                    "VNPay payment failed with code: "
                            + responseCode
            );
        }

        paymentRepository.save(payment);

        // =====================================================
        // STEP 5 — RETURN SUCCESS TO VNPAY
        // =====================================================

        return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
    }


}