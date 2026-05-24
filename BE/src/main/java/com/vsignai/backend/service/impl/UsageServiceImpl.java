package com.vsignai.backend.service.impl;

import com.vsignai.backend.dto.response.UsageTodayResponse;
import com.vsignai.backend.entity.*;
import com.vsignai.backend.enums.feature.FeatureCode;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.repository.UsageLogRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    // FREE LIMIT
    private static final int FREE_LIMIT_SECONDS = 180; // 3 minutes

    public void checkAndSaveUsage(User user, UserSubscription subscription, FeatureCode featureCode, Integer usedSeconds) {

        // Pro month and Pro year => unlimited
        if(subscription != null &&
                subscription.getPlan()
                        .getCode() != PlanCode.FREE) {

            saveUsage(
                    user,
                    subscription,
                    featureCode,
                    usedSeconds
            );

            return;
        }

        Integer usedToday =
                usageLogRepository.getTodayUsedSeconds(
                        user,
                        LocalDate.now()
                );

        if(usedToday + usedSeconds > FREE_LIMIT_SECONDS) {
            throw new AppException(
                    "DAILY_USAGE_LIMIT_EXCEEDED",
                    "Daily usage limit exceeded",
                    HttpStatus.FORBIDDEN
            );

        }

        saveUsage(
                user,
                subscription,
                featureCode,
                usedSeconds
        );
    }

    private void saveUsage(
            User user,
            UserSubscription subscription,
            FeatureCode featureCode,
            Integer usedSeconds
    ) {

        UsageLog usageLog = UsageLog.builder()
                .user(user)
                .subscription(subscription)
                .featureCode(featureCode)
                .usedSeconds(usedSeconds)
                .usageDate(LocalDate.now())
                .build();

        usageLogRepository.save(usageLog);
    }
    @Override
    public UsageTodayResponse getTodayUsage(
            User user
    ) {

        Integer usedSeconds =
                usageLogRepository.getTodayUsedSeconds(
                        user,
                        LocalDate.now()
                );
        UserSubscription subscription =
                subscriptionRepository
                        .findByUserIdAndStatus(
                                user.getId(),
                                SubscriptionStatus.ACTIVE
                        )
                        .stream()
                        .findFirst()
                        .orElse(null);

        PlanCode planCode = PlanCode.FREE;

        if(subscription != null) {
            planCode =
                    subscription.getPlan().getCode();
        }

        // PRO => unlimited
        if(planCode != PlanCode.FREE) {

            return UsageTodayResponse.builder()
                    .plan(planCode.name())
                    .usedSeconds(usedSeconds)
                    .limitSeconds(-1)
                    .remainingSeconds(-1)
                    .usagePercent(0)
                    .build();
        }

        int remaining =
                Math.max(
                        FREE_LIMIT_SECONDS - usedSeconds,
                        0
                );

        int percent =
                (usedSeconds * 100)
                        / FREE_LIMIT_SECONDS;

        return UsageTodayResponse.builder()
                .plan(planCode.name())
                .usedSeconds(usedSeconds)
                .limitSeconds(FREE_LIMIT_SECONDS)
                .remainingSeconds(remaining)
                .usagePercent(percent)
                .build();
    }
}