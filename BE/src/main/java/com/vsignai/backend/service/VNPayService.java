package com.vsignai.backend.service;

import com.vsignai.backend.entity.Payment;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {

    String createPaymentUrl(
            Payment payment,
            HttpServletRequest request
    );

    boolean validateSignature(
            java.util.Map<String, String> params
    );
}