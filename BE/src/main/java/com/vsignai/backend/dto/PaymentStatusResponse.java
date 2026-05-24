package com.vsignai.backend.dto;

import com.vsignai.backend.enums.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusResponse {

    private PaymentStatus status;
}