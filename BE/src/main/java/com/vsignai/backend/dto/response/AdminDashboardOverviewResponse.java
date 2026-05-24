package com.vsignai.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardOverviewResponse {

    private BigDecimal monthlyRevenue;
    private Long totalUsers;
    private Long totalTranslations;
    private Long premiumUsers;

    private List<ChartPointResponse> dailyTranslations;
    private List<ChartPointResponse> monthlyUserGrowth;
}
