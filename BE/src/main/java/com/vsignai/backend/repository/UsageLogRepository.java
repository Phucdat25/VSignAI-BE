package com.vsignai.backend.repository;

import com.vsignai.backend.entity.UsageLog;
import com.vsignai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UsageLogRepository
        extends JpaRepository<UsageLog, Long> {

    @Query("""
    SELECT COALESCE(SUM(u.usedSeconds), 0)
    FROM UsageLog u
    WHERE u.user = :user
    AND u.usageDate = :date
""")
    Integer getTodayUsedSeconds(
            @Param("user") User user,
            @Param("date") LocalDate date
    );

    long count();

    //đếm số lượt dịch
    @Query("""
        SELECT FUNCTION('DATE', u.createdAt), COUNT(u)
        FROM UsageLog u
        WHERE u.createdAt >= :start
        GROUP BY FUNCTION('DATE', u.createdAt)
        ORDER BY FUNCTION('DATE', u.createdAt)
    """)
    List<Object[]> countDailyUsage(
            @Param("start") LocalDateTime start
    );

    //đếm số user hoạt động trong ngày
    @Query("""
    SELECT COUNT(DISTINCT u.user.id)
    FROM UsageLog u
    WHERE u.createdAt >= :start
""")
    long countActiveUsersToday(
            @Param("start") LocalDateTime start
    );

    //biểu đồ user và số lượt dịch
    @Query("""
    SELECT FUNCTION('DATE', u.createdAt),
           COUNT(DISTINCT u.user.id),
           COUNT(u)
    FROM UsageLog u
    WHERE u.createdAt >= :start
      AND u.createdAt < :end
    GROUP BY FUNCTION('DATE', u.createdAt)
    ORDER BY FUNCTION('DATE', u.createdAt)
""")
    List<Object[]> getWeeklyActivity(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}