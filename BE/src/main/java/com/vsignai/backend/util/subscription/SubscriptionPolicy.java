package com.vsignai.backend.util.subscription;

import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.enums.subscription.PlanCode;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

public final class SubscriptionPolicy {

    public static final Set<PlanCode> PRO_PLAN_CODES =
            EnumSet.of(PlanCode.PRO_MONTH, PlanCode.PRO_YEAR);

    private SubscriptionPolicy() {
    }

    public static boolean isFree(SubscriptionPlan plan) {
        return plan.getCode() == PlanCode.FREE
                || plan.getPrice().compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isPro(SubscriptionPlan plan) {
        return PRO_PLAN_CODES.contains(plan.getCode());
    }

    public static boolean isPro(PlanCode code) {
        return PRO_PLAN_CODES.contains(code);
    }

    public static int planPriority(PlanCode code) {
        return switch (code) {
            case PRO_YEAR -> 3;
            case PRO_MONTH -> 2;
            case FREE -> 1;
        };
    }
}
