package com.vsignai.backend.repository;

import com.vsignai.backend.entity.UsageLog;
import com.vsignai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface UsageLogRepository
        extends JpaRepository<UsageLog, Long> {

    @Query("""
        SELECT COALESCE(SUM(u.usedSeconds), 0)
        FROM UsageLog u
        WHERE u.user = :user
        AND u.usageDate = :date
    """)
    Integer getTodayUsedSeconds(
            User user,
            LocalDate date
    );
}
