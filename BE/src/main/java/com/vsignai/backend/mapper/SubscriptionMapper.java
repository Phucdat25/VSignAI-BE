package com.vsignai.backend.mapper;

import com.vsignai.backend.dto.PlanResponse;
import com.vsignai.backend.dto.SubscriptionResponse;
import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.UserSubscription;

public class SubscriptionMapper {

    public static SubscriptionResponse toResponse(UserSubscription entity) {

        if (entity == null) return null;

        return SubscriptionResponse.builder()
                .id(entity.getId())
                .plan(toPlanResponse(entity.getPlan()))
                .status(entity.getStatus())
                .autoRenew(entity.getIsAutoRenew())
                .startedAt(entity.getStartedAt())
                .currentPeriodStart(entity.getCurrentPeriodStart())
                .currentPeriodEnd(entity.getCurrentPeriodEnd())
                .canceledAt(entity.getCanceledAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private static PlanResponse toPlanResponse(SubscriptionPlan plan) {

        if (plan == null) return null;

        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode().name())
                .name(plan.getName())
                .price(plan.getPrice())
                .currency(plan.getCurrency())
                .intervalUnit(plan.getIntervalUnit().name())
                .intervalCount(plan.getIntervalCount())
                .build();
    }
}