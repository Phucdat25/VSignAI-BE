package com.vsignai.backend.scheduler;

import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.util.subscription.SubscriptionStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final UserSubscriptionRepository repository;
    private final SubscriptionStateManager subscriptionStateManager;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void expireSubscriptions() {

        LocalDateTime now = LocalDateTime.now();

        List<UserSubscription> expiredSubs =
                repository.findAllByStatusAndCurrentPeriodEndBefore(
                        SubscriptionStatus.ACTIVE,
                        now
                );

        for (UserSubscription sub : expiredSubs) {

            subscriptionStateManager.expire(sub);

            log.info(
                    "Subscription expired | subscriptionId={} | userId={}",
                    sub.getId(),
                    sub.getUser().getId()
            );
        }
    }
}