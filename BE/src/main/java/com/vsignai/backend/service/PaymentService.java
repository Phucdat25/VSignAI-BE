package com.vsignai.backend.service;

import com.vsignai.backend.dto.PaymentInitRequest;
import com.vsignai.backend.dto.response.PaymentInitResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    PaymentInitResponse initiatePayment(
            PaymentInitRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    );
}