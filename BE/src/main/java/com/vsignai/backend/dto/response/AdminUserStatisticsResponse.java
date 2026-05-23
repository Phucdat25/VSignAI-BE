package com.vsignai.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserStatisticsResponse {

    private long totalUsers;
    private long premiumUsers;
    private long activeToday;
}