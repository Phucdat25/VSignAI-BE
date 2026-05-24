package com.vsignai.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueResponse {

    private String type;
    private String label;
    private BigDecimal revenue;
}