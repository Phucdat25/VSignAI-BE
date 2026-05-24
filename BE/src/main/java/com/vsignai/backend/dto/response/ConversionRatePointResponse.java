package com.vsignai.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionRatePointResponse {

    private String label;

    private Long totalUsers;

    private Long premiumUsers;

    private Double conversionRate;
}
