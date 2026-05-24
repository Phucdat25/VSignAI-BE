package com.vsignai.backend.util.subscription;

import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionFactory {

    private final BillingPeriodCalculator billingPeriodCalculator;

    public UserSubscription create(
            User user,
            SubscriptionPlan plan
    ) {

        LocalDateTime now = LocalDateTime.now();

        boolean autoRenew =
                !PlanCode.FREE.equals(plan.getCode());

        return UserSubscription.builder()
                .user(user)
                .plan(plan)
                .status(resolveInitialStatus(plan))
                .startedAt(now)
                .currentPeriodStart(now)
                .currentPeriodEnd(
                        billingPeriodCalculator.calculatePeriodEnd(
                                now,
                                plan
                        )
                )
                .isAutoRenew(autoRenew)
                .cancelAtPeriodEnd(false)
                .build();
    }
    private SubscriptionStatus resolveInitialStatus(
            SubscriptionPlan plan
    ) {

        return SubscriptionPolicy.isFree(plan)
                ? SubscriptionStatus.ACTIVE
                : SubscriptionStatus.PENDING;
    }
}