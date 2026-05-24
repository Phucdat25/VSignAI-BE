package com.vsignai.backend.util.subscription;

import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.repository.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final UserSubscriptionRepository subscriptionRepository;

    public void validatePlanActive(SubscriptionPlan plan) {

        if (Boolean.FALSE.equals(plan.getIsActive())) {

            throw new AppException(
                    "PLAN_INACTIVE",
                    "Plan is inactive",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateCreateSubscription(Long userId, SubscriptionPlan plan) {

        if (SubscriptionPolicy.isFree(plan)) {
            validateFreePlanCreate(userId);
            return;
        }

        if (SubscriptionPolicy.isPro(plan)) {
            validateProUpgrade(userId, plan.getCode());
        }
    }

    private void validateFreePlanCreate(Long userId) {

        if (subscriptionRepository.existsByUserIdAndPlan_CodeAndStatus(
                userId,
                PlanCode.FREE,
                SubscriptionStatus.ACTIVE
        )) {
            throw new AppException(
                    "FREE_SUBSCRIPTION_ALREADY_ACTIVE",
                    "Free subscription is already active",
                    HttpStatus.BAD_REQUEST
            );
        }

        throw new AppException(
                "FREE_SUBSCRIPTION_AUTO_PROVISIONED",
                "Free subscription is provisioned automatically on registration",
                HttpStatus.BAD_REQUEST
        );
    }

    private void validateProUpgrade(Long userId, PlanCode targetPlan) {

        boolean hasFreeActive =
                subscriptionRepository.existsByUserIdAndPlan_CodeAndStatus(
                        userId,
                        PlanCode.FREE,
                        SubscriptionStatus.ACTIVE
                );

        if (!hasFreeActive) {
            throw new AppException(
                    "FREE_SUBSCRIPTION_REQUIRED",
                    "An active free subscription is required before upgrading to a pro plan",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (subscriptionRepository.existsByUserIdAndPlan_CodeInAndStatus(
                userId,
                SubscriptionPolicy.PRO_PLAN_CODES,
                SubscriptionStatus.PENDING
        )) {
            throw new AppException(
                    "PRO_SUBSCRIPTION_PENDING_EXISTS",
                    "A pro subscription upgrade is already pending payment",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (subscriptionRepository.existsByUserIdAndPlan_CodeInAndStatus(
                userId,
                SubscriptionPolicy.PRO_PLAN_CODES,
                SubscriptionStatus.ACTIVE
        )) {
            throw new AppException(
                    "PRO_SUBSCRIPTION_ALREADY_ACTIVE",
                    "User already has an active pro subscription",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (subscriptionRepository.existsByUserIdAndPlan_CodeAndStatus(
                userId,
                targetPlan,
                SubscriptionStatus.PENDING
        )) {
            throw new AppException(
                    "SUBSCRIPTION_ALREADY_PENDING",
                    "This pro plan is already pending payment",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validatePayable(UserSubscription sub) {

        if (sub.getStatus() != SubscriptionStatus.PENDING) {
            throw new AppException(
                    "SUBSCRIPTION_NOT_PENDING",
                    "Only pending subscriptions can be paid",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!SubscriptionPolicy.isPro(sub.getPlan())) {
            throw new AppException(
                    "SUBSCRIPTION_NOT_PAYABLE",
                    "Only pro subscriptions require payment",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateCancelable(UserSubscription sub) {

        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {

            throw new AppException(
                    "SUBSCRIPTION_NOT_ACTIVE",
                    "Only active subscription can be canceled",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateStatusTransition(
            SubscriptionStatus from,
            SubscriptionStatus to
    ) {

        if (from == to) {
            return;
        }

        boolean valid = switch (from) {

            case PENDING ->
                    to == SubscriptionStatus.ACTIVE
                            || to == SubscriptionStatus.CANCELED
                            || to == SubscriptionStatus.PAST_DUE;

            case TRIALING ->
                    to == SubscriptionStatus.ACTIVE
                            || to == SubscriptionStatus.CANCELED
                            || to == SubscriptionStatus.EXPIRED;

            case ACTIVE ->
                    to == SubscriptionStatus.PAST_DUE
                            || to == SubscriptionStatus.CANCELED
                            || to == SubscriptionStatus.EXPIRED
                            || to == SubscriptionStatus.PAUSED;

            case PAST_DUE ->
                    to == SubscriptionStatus.ACTIVE
                            || to == SubscriptionStatus.CANCELED
                            || to == SubscriptionStatus.EXPIRED;

            case PAUSED ->
                    to == SubscriptionStatus.ACTIVE
                            || to == SubscriptionStatus.CANCELED;

            case EXPIRED ->
                    false;

            case CANCELED ->
                    false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid subscription transition: "
                            + from + " -> " + to
            );
        }
    }
}
