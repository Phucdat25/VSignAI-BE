package com.vsignai.backend.service;

import com.vsignai.backend.entity.Payment;

import java.util.Map;

public interface VNPayGatewayService {

    String createPaymentUrl(
            Payment payment
    );

    boolean verifyCallback(
            Map<String, String> params
    );

    boolean validateMerchant(Map<String, String> params);
}
