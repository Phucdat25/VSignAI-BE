package com.vsignai.backend.dto;

import com.vsignai.backend.enums.subscription.IntervalUnit;
import com.vsignai.backend.enums.subscription.PlanCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SubscriptionPlanRequest {

    private PlanCode code;

    private String name;

    private BigDecimal price;

    private String currency;

    private IntervalUnit intervalUnit;

    private Integer intervalCount;

    private Boolean isActive;
}