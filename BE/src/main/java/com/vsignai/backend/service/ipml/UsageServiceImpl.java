package com.vsignai.backend.service.ipml;

import com.vsignai.backend.entity.*;
import com.vsignai.backend.enums.feature.FeatureCode;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.repository.UsageLogRepository;
import com.vsignai.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;

    // FREE LIMIT
    private static final int FREE_LIMIT_SECONDS = 300;

    public void checkAndSaveUsage(
            User user,
            UserSubscription subscription,
            FeatureCode featureCode,
            Integer usedSeconds
    ) {

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
            throw new RuntimeException(
                    "Daily usage limit exceeded"
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
}
