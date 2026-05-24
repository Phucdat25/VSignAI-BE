package com.vsignai.backend.dto;

import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionResponse {

    private Long id;

    private PlanResponse plan;

    private SubscriptionStatus status;

    private Boolean autoRenew;

    private LocalDateTime startedAt;

    private LocalDateTime currentPeriodStart;

    private LocalDateTime currentPeriodEnd;

    private LocalDateTime canceledAt;

    private LocalDateTime createdAt;
}