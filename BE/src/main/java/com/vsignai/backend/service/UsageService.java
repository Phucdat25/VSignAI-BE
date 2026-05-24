package com.vsignai.backend.service;

import com.vsignai.backend.dto.response.UsageTodayResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.feature.FeatureCode;

public interface UsageService {
    public void checkAndSaveUsage(User user, UserSubscription subscription, FeatureCode featureCode, Integer usedSeconds);
    UsageTodayResponse getTodayUsage(
            User user
    );
}
