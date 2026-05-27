package com.vsignai.backend.dto;

import com.vsignai.backend.enums.subscription.PlanCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSubscriptionRequest {

    @NotNull(message = "Plan code is required")
    private PlanCode planCode;
}