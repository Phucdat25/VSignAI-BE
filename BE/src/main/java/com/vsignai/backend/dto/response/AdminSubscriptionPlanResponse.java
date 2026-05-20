package com.vsignai.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubscriptionPlanResponse {

    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private String currency;
    private String intervalUnit;
    private Integer intervalCount;
    private Boolean isActive;

    private Long activeUserCount;
}
