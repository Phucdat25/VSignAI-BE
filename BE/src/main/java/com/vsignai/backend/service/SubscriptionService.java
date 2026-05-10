package com.vsignai.backend.service;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;

public interface SubscriptionService {
    public UserSubscription getActiveSubscription(User user);
}
