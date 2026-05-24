package com.vsignai.backend.service;

import com.vsignai.backend.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(
            Long userId,
            Long subscriptionId,
            CreatePaymentRequest request
    );

    List<PaymentResponse> getBySubscription(
            Long userId,
            Long subscriptionId
    );

    PaymentResponse renew(
            Long userId,
            Long subscriptionId
    );



    PaymentStatusResponse getPaymentStatus(String txnRef);

    VNPayIPNResponse handleVNPayIPN(HttpServletRequest request);

    boolean handleVNPayReturn(java.util.Map<String, String> params);
}
