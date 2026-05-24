package com.vsignai.backend.repository;

import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vsignai.backend.enums.subscription.PlanCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    // 🔹 Lấy tất cả subscription của user
    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 🔹 Lấy subscription hiện tại (ACTIVE / TRIALING)
    Optional<UserSubscription> findFirstByUserIdAndStatusInOrderByCurrentPeriodEndDesc(
            Long userId,
            List<SubscriptionStatus> statuses
    );

    // 🔹 Check user có subscription active không
    Boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // 🔹 Lấy subscription theo id + user (security)
    Optional<UserSubscription> findByIdAndUserId(Long id, Long userId);

    List<UserSubscription> findAllByStatusAndCurrentPeriodEndBefore(
            SubscriptionStatus status,
            LocalDateTime now
    );

    List<UserSubscription> findByUserIdAndStatus(
            Long userId,
            SubscriptionStatus status
    );

    boolean existsByUserIdAndPlan_CodeAndStatus(
            Long userId,
            PlanCode code,
            SubscriptionStatus status
    );

    boolean existsByUserIdAndPlan_CodeInAndStatus(
            Long userId,
            Collection<PlanCode> codes,
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


    Optional<UserSubscription>
    findTopByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            SubscriptionStatus status
    );


}