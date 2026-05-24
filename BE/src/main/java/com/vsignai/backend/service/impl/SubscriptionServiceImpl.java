package com.vsignai.backend.service.impl;

import com.vsignai.backend.config.RequestContext;
import com.vsignai.backend.dto.SubscriptionResponse;
import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.mapper.SubscriptionMapper;
import com.vsignai.backend.repository.SubscriptionPlanRepository;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;

import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.util.subscription.SubscriptionFactory;
import com.vsignai.backend.util.subscription.SubscriptionPolicy;
import com.vsignai.backend.util.subscription.SubscriptionStateManager;
import com.vsignai.backend.util.subscription.SubscriptionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;

    private final SubscriptionPlanRepository planRepository;

    private final UserSubscriptionRepository subscriptionRepository;

    private final SubscriptionValidator subscriptionValidator;

    private final SubscriptionStateManager subscriptionStateManager;

    private final SubscriptionFactory subscriptionFactory;



    @Override
    public List<SubscriptionResponse> getAll(Long userId) {

        return subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }

    @Override
    public SubscriptionResponse getCurrent(Long userId) {

        List<UserSubscription> activeSubs =
                subscriptionRepository.findByUserIdAndStatus(
                        userId,
                        SubscriptionStatus.ACTIVE
                );

        if (activeSubs.isEmpty()) {
            throw new AppException(
                    "NO_ACTIVE_SUBSCRIPTION",
                    "No active subscription",
                    HttpStatus.NOT_FOUND
            );
        }

        UserSubscription current =
                activeSubs.stream()
                        .max(Comparator.comparingInt(
                                sub -> SubscriptionPolicy.planPriority(
                                        sub.getPlan().getCode()
                                )
                        ))
                        .orElseThrow();

        return SubscriptionMapper.toResponse(current);
    }

    @Override
    @Transactional
    public SubscriptionResponse create(
            Long userId,
            Long planId
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "USER_NOT_FOUND",
                                        "User not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        SubscriptionPlan plan =
                planRepository.findById(planId)
                        .orElseThrow(() ->
                                new AppException(
                                        "PLAN_NOT_FOUND",
                                        "Plan not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        subscriptionValidator.validatePlanActive(plan);
        subscriptionValidator.validateCreateSubscription(userId, plan);

        UserSubscription sub =
                subscriptionFactory.create(user, plan);

        subscriptionRepository.save(sub);

        log.info(
                "Subscription created | requestId={} | userId={} | subscriptionId={} | plan={} | status={}",
                RequestContext.getRequestId(),
                userId,
                sub.getId(),
                plan.getCode(),
                sub.getStatus()
        );

        return SubscriptionMapper.toResponse(sub);
    }

    @Override
    @Transactional
    public void provisionFreeSubscriptionIfAbsent(Long userId) {

        if (subscriptionRepository.existsByUserIdAndPlan_CodeAndStatus(
                userId,
                PlanCode.FREE,
                SubscriptionStatus.ACTIVE
        )) {
            return;
        }

        if (subscriptionRepository.existsByUserIdAndPlan_CodeInAndStatus(
                userId,
                SubscriptionPolicy.PRO_PLAN_CODES,
                SubscriptionStatus.ACTIVE
        )) {
            return;
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "USER_NOT_FOUND",
                                        "User not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        SubscriptionPlan freePlan =
                planRepository.findByCode(PlanCode.FREE)
                        .orElseThrow(() ->
                                new AppException(
                                        "FREE_PLAN_NOT_FOUND",
                                        "Free plan is not configured",
                                        HttpStatus.INTERNAL_SERVER_ERROR
                                )
                        );

        subscriptionValidator.validatePlanActive(freePlan);

        UserSubscription sub =
                subscriptionFactory.create(user, freePlan);

        subscriptionRepository.save(sub);

        log.info(
                "Free subscription provisioned | requestId={} | userId={} | subscriptionId={}",
                RequestContext.getRequestId(),
                userId,
                sub.getId()
        );
    }

    @Override
    @Transactional
    public SubscriptionResponse cancel(
            Long userId,
            Long subscriptionId
    ) {

        UserSubscription sub =
                subscriptionRepository
                        .findByIdAndUserId(subscriptionId, userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "SUBSCRIPTION_NOT_FOUND",
                                        "Subscription not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        subscriptionValidator.validateCancelable(sub);

        subscriptionStateManager.cancel(sub);

        subscriptionRepository.save(sub);

        log.info(
                "Subscription canceled | requestId={} | userId={} | subscriptionId={}",
                RequestContext.getRequestId(),
                userId,
                subscriptionId
        );

        return SubscriptionMapper.toResponse(sub);
    }

    @Override
    public UserSubscription getActiveSubscription(Long userId) {

        return subscriptionRepository
                .findTopByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        SubscriptionStatus.ACTIVE
                )
                .orElse(null);
    }
}
