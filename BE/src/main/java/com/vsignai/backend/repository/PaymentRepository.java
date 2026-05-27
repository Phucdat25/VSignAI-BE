package com.vsignai.backend.repository;

import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.entity.Payment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

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

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    boolean existsByGatewayTransactionId(String gatewayTransactionId);

    List<Payment> findByStatusAndExpiresAtBefore(
            PaymentStatus status,
            LocalDateTime time
    );

    Optional<Payment>
    findTopBySubscription_User_IdAndStatusOrderByCreatedAtDesc(
            Long userId,
            PaymentStatus status
    );

    Optional<Payment> findByIdempotencyKey(String key);



    Optional<Payment>
    findTopBySubscription_User_IdAndStatusAndExpiresAtAfterOrderByCreatedAtDesc(
            Long userId,
            PaymentStatus status,
            LocalDateTime now
    );
}