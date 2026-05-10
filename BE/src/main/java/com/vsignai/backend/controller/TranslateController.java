package com.vsignai.backend.controller;


import com.vsignai.backend.dto.RecognitionRequest;
import com.vsignai.backend.dto.TranslateRequest;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.feature.FeatureCode;
import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.service.UsageService;
import com.vsignai.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/translate")
@RequiredArgsConstructor
public class TranslateController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final UsageService usageService;

    // TEXT -> SIGN
    @PostMapping("/text-to-sign")
    public String textToSign(
            Authentication authentication,
            @RequestBody TranslateRequest request
    ) {

        User user =
                userService.findByEmail(
                        authentication.getName()
                );

        UserSubscription subscription =
                subscriptionService
                        .getActiveSubscription(user);

        usageService.checkAndSaveUsage(
                user,
                subscription,
                FeatureCode.TRANSLATION,
                request.getUsedSeconds()
        );

        // TODO AI xử lý thật
        return "Text translated successfully";
    }

    // SIGN -> TEXT
    @PostMapping("/sign-to-text")
    public String signToText(
            Authentication authentication,
            @RequestBody RecognitionRequest request
    ) {

        User user =
                userService.findByEmail(
                        authentication.getName()
                );

        UserSubscription subscription =
                subscriptionService
                        .getActiveSubscription(user);

        usageService.checkAndSaveUsage(
                user,
                subscription,
                FeatureCode.SIGN_RECOGNITION,
                request.getUsedSeconds()
        );

        // TODO AI xử lý thật
        return "Sign recognized successfully";
    }
}