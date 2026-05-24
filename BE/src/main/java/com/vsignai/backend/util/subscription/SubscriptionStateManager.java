package com.vsignai.backend.util.subscription;

import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.repository.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionStateManager {

    private final BillingPeriodCalculator billingPeriodCalculator;
    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator subscriptionValidator;

    public void activate(UserSubscription sub) {

        LocalDateTime now = LocalDateTime.now();

        transition(sub, SubscriptionStatus.ACTIVE);

        sub.setCurrentPeriodStart(now);

        sub.setCurrentPeriodEnd(
                billingPeriodCalculator.calculatePeriodEnd(
                        now,
                        sub.getPlan()
                )
        );
    }

    public void activatePaidAndSupersedeFree(UserSubscription proSub) {

        Long userId = proSub.getUser().getId();

        activate(proSub);
        supersedeActiveFreeSubscriptions(userId, proSub.getId());
    }

    public void supersedeActiveFreeSubscriptions(Long userId, Long excludeSubscriptionId) {

        List<UserSubscription> freeActive =
                subscriptionRepository.findByUserIdAndStatus(
                                userId,
                                SubscriptionStatus.ACTIVE
                        )
                        .stream()
                        .filter(sub -> sub.getPlan().getCode() == PlanCode.FREE)
                        .filter(sub -> !sub.getId().equals(excludeSubscriptionId))
                        .toList();

        for (UserSubscription freeSub : freeActive) {
            expire(freeSub);
            subscriptionRepository.save(freeSub);
        }
    }

    public void renew(UserSubscription sub) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime base = sub.getCurrentPeriodEnd();

        if (base == null || base.isBefore(now)) {
            base = now;
        }

        sub.setCurrentPeriodEnd(
                billingPeriodCalculator.calculatePeriodEnd(
                        base,
                        sub.getPlan()
                )
        );
    }

    public void cancel(UserSubscription sub) {

        LocalDateTime now = LocalDateTime.now();

        sub.setCancelAtPeriodEnd(true);
        sub.setCanceledAt(now);
        sub.setIsAutoRenew(false);
    }

    public void expire(UserSubscription sub) {

        LocalDateTime now = LocalDateTime.now();

        transition(sub, SubscriptionStatus.EXPIRED);
        sub.setCanceledAt(now);
        sub.setIsAutoRenew(false);
        sub.setCancelAtPeriodEnd(false);
    }

    private void transition(
            UserSubscription sub,
            SubscriptionStatus target
    ) {

        subscriptionValidator.validateStatusTransition(
                sub.getStatus(),
                target
        );

        sub.setStatus(target);
    }
}
