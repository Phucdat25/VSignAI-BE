package com.vsignai.backend.service;

import com.vsignai.backend.dto.response.*;
import com.vsignai.backend.enums.RevenueFilterType;
import com.vsignai.backend.enums.user.UserStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface AdminDashboardService {

    AdminUserStatisticsResponse getStatistics();

    Page<AdminUserResponse> getUsers(int page, int size);

    AdminUserResponse getUserDetail(Long id);

    void updateUserStatus(Long id, UserStatus status);
    AdminDashboardOverviewResponse getOverview();

    RevenueResponse getRevenue(
            RevenueFilterType type,
            Integer year,
            Integer month,
            LocalDate date
    );

    List<ConversionRatePointResponse> getMonthlyConversionRate(Integer year);

    public List<WeeklyActivityResponse> getWeeklyActivity();
    public List<MonthlyUserGrowthResponse> getMonthlyUserGrowth(Integer year);
    AdminUserResponse createUser(AdminCreateUserRequest request);
}