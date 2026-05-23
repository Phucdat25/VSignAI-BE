package com.vsignai.backend.repository;

import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.enums.subscription.PlanCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

        Optional<SubscriptionPlan> findByCode(PlanCode code);

        boolean existsByCode(PlanCode code);
}