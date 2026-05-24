package com.vsignai.backend.service;

import com.vsignai.backend.dto.SubscriptionPlanRequest;
import com.vsignai.backend.dto.response.AdminSubscriptionPlanResponse;

import java.util.List;

public interface AdminSubscriptionPlanService {

    List<AdminSubscriptionPlanResponse> getAllPlans();

    AdminSubscriptionPlanResponse createPlan(SubscriptionPlanRequest request);

    AdminSubscriptionPlanResponse updatePlan(Long id, SubscriptionPlanRequest request);

    void updatePlanStatus(Long id, Boolean isActive);
}