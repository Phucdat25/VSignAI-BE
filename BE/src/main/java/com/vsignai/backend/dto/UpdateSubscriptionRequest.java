package com.vsignai.backend.dto;

import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import lombok.Data;
import jakarta.validation.constraints.NotNull;


@Data
public class UpdateSubscriptionRequest {

    @NotNull(message = "Status is required")
    private SubscriptionStatus status; // ✅ enum
}