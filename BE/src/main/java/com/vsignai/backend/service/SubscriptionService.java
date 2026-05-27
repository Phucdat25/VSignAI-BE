package com.vsignai.backend.service;

import com.vsignai.backend.dto.CreateSubscriptionRequest;
import com.vsignai.backend.dto.response.CreateSubscriptionResponse;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import jakarta.servlet.http.HttpServletRequest;

public interface SubscriptionService {

    UserSubscription getActiveSubscription(User user);

    CreateSubscriptionResponse createSubscription(
            User user,
            CreateSubscriptionRequest request,
            String idempotencyKey,
            HttpServletRequest httpRequest

    );

    void activateSubscription(Payment payment);
}