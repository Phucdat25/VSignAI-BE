package com.vsignai.backend.mapper;

import com.vsignai.backend.dto.PaymentResponse;
import com.vsignai.backend.entity.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment entity) {

        if (entity == null) return null;

        return PaymentResponse.builder()
                .id(entity.getId())
                .subscriptionId(entity.getSubscription().getId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .paymentGateway(entity.getGateway())
                .paidAt(entity.getPaidAt())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
