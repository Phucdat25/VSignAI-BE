package com.vsignai.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageTodayResponse {

    private String plan;

    private Integer usedSeconds;

    private Integer limitSeconds;

    private Integer remainingSeconds;

    private Integer usagePercent;
}