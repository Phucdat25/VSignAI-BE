package com.vsignai.backend.repository;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserAndStatus(
            User user,
            SubscriptionStatus status
    );
}