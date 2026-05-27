package com.vsignai.backend.repository;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserAndStatus(
            User user,
            SubscriptionStatus status
    );
    Optional<UserSubscription> findTopByUserIdAndStatusAndPlan_CodeInOrderByCreatedAtDesc(
            Long userId,
            SubscriptionStatus status,
            List<PlanCode> planCodes
    );

    long countByPlan_CodeInAndStatus(
            List<PlanCode> planCodes,
            SubscriptionStatus status
    );

    //đếm premium user theo tháng
    @Query("""
    SELECT MONTH(us.createdAt), COUNT(DISTINCT us.user.id)
    FROM UserSubscription us
    WHERE YEAR(us.createdAt) = :year
      AND us.status = 'ACTIVE'
      AND us.plan.code IN :plans
    GROUP BY MONTH(us.createdAt)
    ORDER BY MONTH(us.createdAt)
""")
    List<Object[]> countPremiumUsersByMonth(
            @Param("year") Integer year,
            @Param("plans") List<PlanCode> plans
    );

    long countByPlan_IdAndStatus(
            Long planId,
            SubscriptionStatus status
    );

    @Query("""
    SELECT us
    FROM UserSubscription us
    WHERE us.user.id = :userId
      AND us.status = 'ACTIVE'
      AND us.currentPeriodEnd > CURRENT_TIMESTAMP
    ORDER BY us.currentPeriodEnd DESC
""")
    Optional<UserSubscription> findCurrentActiveSubscription(
            @Param("userId") Long userId
    );
}