package com.vsignai.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateSubscriptionRequest {

    @NotNull(message = "PlanId is required")
    private Long planId;

    private Boolean autoRenew = true;
}
