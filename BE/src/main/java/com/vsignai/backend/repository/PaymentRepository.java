package com.vsignai.backend.repository;

import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.enums.payment.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findFirstBySubscription_IdAndStatusOrderByCreatedAtDesc(
            Long subscriptionId,
            PaymentStatus status
    );

    @Query("""
            SELECT p FROM Payment p
            WHERE p.subscription.id = :subscriptionId
              AND p.status = :status
              AND (p.expiresAt IS NULL OR p.expiresAt > :now)
            ORDER BY p.createdAt DESC
            """)
    Optional<Payment> findActivePendingBySubscriptionId(
            @Param("subscriptionId") Long subscriptionId,
            @Param("status") PaymentStatus status,
            @Param("now") LocalDateTime now
    );

    List<Payment> findBySubscription_IdOrderByCreatedAtDesc(Long subscriptionId);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.gatewayTransactionId = :gatewayTransactionId")
    Optional<Payment> findByGatewayTransactionIdForUpdate(
            @Param("gatewayTransactionId") String gatewayTransactionId
    );

    List<Payment> findAllByStatusAndExpiresAtBefore(
            PaymentStatus status,
            LocalDateTime expiresAt
    );

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = :status
          AND p.createdAt >= :start
          AND p.createdAt < :end
    """)
    BigDecimal sumRevenueByStatusAndCreatedAtBetween(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    // doanh thu tháng
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'SUCCESS'
          AND p.createdAt >= :start
          AND p.createdAt < :end
    """)
    BigDecimal getMonthlyRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    //doanh thu theo ngày tháng năm có filter
    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.status = :status
      AND p.createdAt >= :start
      AND p.createdAt < :end
""")
    BigDecimal sumRevenue(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
