package com.vsignai.backend.repository;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByDeletedAtIsNull();

    long countByStatusAndDeletedAtIsNull(UserStatus status);

    Page<User> findByDeletedAtIsNull(Pageable pageable);

    //đếm người dùng hàng tháng
    @Query("""
    SELECT MONTH(u.createdAt), COUNT(u)
    FROM User u
    WHERE u.deletedAt IS NULL
      AND u.createdAt >= :start
    GROUP BY MONTH(u.createdAt)
    ORDER BY MONTH(u.createdAt)
""")
    List<Object[]> countMonthlyUsers(
            @Param("start") LocalDateTime start
    );

    //đếm user theo tháng
    @Query("""
    SELECT MONTH(u.createdAt), COUNT(u)
    FROM User u
    WHERE YEAR(u.createdAt) = :year
      AND u.deletedAt IS NULL
    GROUP BY MONTH(u.createdAt)
    ORDER BY MONTH(u.createdAt)
""")
    List<Object[]> countUsersByMonth(
            @Param("year") Integer year
    );


}
