package com.vsignai.backend.controller;

import com.vsignai.backend.dto.PaymentInitRequest;
import com.vsignai.backend.dto.response.PaymentInitResponse;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.service.PaymentService;
import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final PaymentService  paymentService;

    @GetMapping("/vnpay-return")
    @Transactional
    public String vnpayReturn(
            @RequestParam Map<String, String> params
    ) {
        boolean validSignature = vnPayService.validateSignature(params);

        if (!validSignature) {
            return "Invalid signature";
        }

        String transactionId = params.get("vnp_TxnRef");

        Payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayTransactionId(params.get("vnp_TransactionNo"));
            payment.setGatewayResponseCode(responseCode);

            paymentRepository.save(payment);

            subscriptionService.activateSubscription(payment);
        }

        return "VNPay payment completed";
    }

    @RequestMapping(
            value = "/vnpay-ipn",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @Transactional
    public String vnpayIpn(
            @RequestParam Map<String, String> params
    ) {
        System.out.println("========== VNPAY IPN ==========");
        System.out.println(params);


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

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitResponse> initiatePayment(
            @RequestBody PaymentInitRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        PaymentInitResponse response = paymentService.initiatePayment(
                request,
                authentication,
                httpRequest
        );

        return ResponseEntity.ok(response);
    }


}