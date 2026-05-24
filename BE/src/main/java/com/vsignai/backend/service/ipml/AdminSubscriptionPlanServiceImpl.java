package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.SubscriptionPlanRequest;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.dto.response.AdminSubscriptionPlanResponse;
import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.SubscriptionPlanRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.AdminSubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSubscriptionPlanServiceImpl implements AdminSubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Override
    public List<AdminSubscriptionPlanResponse> getAllPlans() {
        return subscriptionPlanRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminSubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        if (subscriptionPlanRepository.existsByCode(request.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "PLAN_CODE_ALREADY_EXISTS");
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .code(request.getCode())
                .name(request.getName())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .intervalUnit(request.getIntervalUnit())
                .intervalCount(request.getIntervalCount())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return mapToResponse(subscriptionPlanRepository.save(plan));
    }

    @Override
    public AdminSubscriptionPlanResponse updatePlan(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "PLAN_NOT_FOUND"));

        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setCurrency(request.getCurrency());
        plan.setIntervalUnit(request.getIntervalUnit());
        plan.setIntervalCount(request.getIntervalCount());

        if (request.getIsActive() != null) {
            plan.setIsActive(request.getIsActive());
        }

        return mapToResponse(subscriptionPlanRepository.save(plan));
    }

    @Override
    public void updatePlanStatus(Long id, Boolean isActive) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "PLAN_NOT_FOUND"));

        plan.setIsActive(isActive);
        subscriptionPlanRepository.save(plan);
    }

    private AdminSubscriptionPlanResponse mapToResponse(SubscriptionPlan plan) {
        Long activeUserCount = userSubscriptionRepository.countByPlan_IdAndStatus(
                plan.getId(),
                SubscriptionStatus.ACTIVE
        );

        return AdminSubscriptionPlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode().name())
                .name(plan.getName())
                .price(plan.getPrice())
                .currency(plan.getCurrency())
                .intervalUnit(plan.getIntervalUnit().name())
                .intervalCount(plan.getIntervalCount())
                .isActive(plan.getIsActive())
                .activeUserCount(activeUserCount)
                .build();
    }
}
