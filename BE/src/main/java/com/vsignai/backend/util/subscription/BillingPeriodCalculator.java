package com.vsignai.backend.util.subscription;

import com.vsignai.backend.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BillingPeriodCalculator {

    public LocalDateTime calculatePeriodEnd(
            LocalDateTime start,
            SubscriptionPlan plan
    ) {

        if (plan.getIntervalUnit() == null
                || plan.getIntervalCount() == null) {

            throw new IllegalStateException(
                    "Invalid subscription interval"
            );
        }

        return switch (plan.getIntervalUnit()) {

            case MONTH ->
                    start.plusMonths(plan.getIntervalCount());

            case YEAR ->
                    start.plusYears(plan.getIntervalCount());
        };
    }
}