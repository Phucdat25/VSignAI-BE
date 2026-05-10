package com.vsignai.backend.service.ipml;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscription getActiveSubscription(User user) {

        return userSubscriptionRepository
                .findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .orElse(null);
    }


}
