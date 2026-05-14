package com.vsignai.backend.repository;

import com.vsignai.backend.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {
}