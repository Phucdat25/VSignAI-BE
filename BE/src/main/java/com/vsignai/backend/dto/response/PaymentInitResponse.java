package com.vsignai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitResponse {
    private String paymentUrl; // URL to redirect to VNPay
    private String transactionId; // For reference
}
