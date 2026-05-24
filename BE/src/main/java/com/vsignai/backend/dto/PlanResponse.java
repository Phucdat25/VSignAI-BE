package com.vsignai.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlanResponse {

    private Long id;

    private String code;

    private String name;

    private BigDecimal price;

    private String currency;

    private String intervalUnit;

    private Integer intervalCount;
}