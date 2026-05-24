package com.vsignai.backend.service;

import com.vsignai.backend.dto.VNPayWebhookRequest;

public interface PaymentGatewayService {

    boolean verifySignature(
            VNPayWebhookRequest request,
            String signature
    );
}
