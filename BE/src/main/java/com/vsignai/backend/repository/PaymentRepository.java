package com.vsignai.backend.repository;

import com.vsignai.backend.enums.PaymentStatus;
import com.vsignai.backend.entity.Payment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}