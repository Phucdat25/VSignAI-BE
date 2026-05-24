package com.vsignai.backend.service;

import com.vsignai.backend.dto.SubscriptionResponse;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.entity.User;


import java.util.List;

public interface SubscriptionService {

    UserSubscription getActiveSubscription(Long userId);

    List<SubscriptionResponse> getAll(Long userId);

    SubscriptionResponse getCurrent(Long userId);

    SubscriptionResponse create(Long userId, Long planId);

    SubscriptionResponse cancel(Long userId, Long subscriptionId);

    void provisionFreeSubscriptionIfAbsent(Long userId);
}