package com.vsignai.backend.dto;

import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;

    private Long subscriptionId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status; // ✅ enum

    private PaymentMethod paymentMethod;

    private LocalDateTime paidAt;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    private LocalDateTime createdAt;

    private String paymentUrl;

    private PaymentGateway paymentGateway;
}
