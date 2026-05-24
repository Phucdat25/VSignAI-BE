package com.vsignai.backend.controller;

import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.dto.response.*;
import com.vsignai.backend.enums.RevenueFilterType;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.service.AdminDashboardService;
import com.vsignai.backend.util.ResponseFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/statistics")
    public ApiResponse<AdminUserStatisticsResponse> getStatistics() {

        return ResponseFactory.success(
                adminDashboardService.getStatistics()
        );
    }

    @GetMapping
    public ApiResponse<Page<AdminUserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseFactory.success(
                adminDashboardService.getUsers(page, size)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUserResponse> getUserDetail(
            @PathVariable Long id
    ) {

        return ResponseFactory.success(
                adminDashboardService.getUserDetail(id)
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status
    ) {

        adminDashboardService.updateUserStatus(id, status);

        return ResponseFactory.successMessage(
                "User status updated successfully"
        );
    }

    @GetMapping("/overview")
    public ApiResponse<AdminDashboardOverviewResponse> getOverview() {

        return ResponseFactory.success(
                adminDashboardService.getOverview()
        );
    }

    @GetMapping("/revenue")
    public ApiResponse<RevenueResponse> getRevenue(
            @RequestParam RevenueFilterType type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) LocalDate date
    ) {

        return ResponseFactory.success(
                adminDashboardService.getRevenue(type, year, month, date)
        );
    }

    @GetMapping("/conversion-rate")
    public ApiResponse<List<ConversionRatePointResponse>>
    getConversionRate(
            @RequestParam(required = false) Integer year
    ) {

        return ResponseFactory.success(
                adminDashboardService.getMonthlyConversionRate(year)
        );
    }

    @GetMapping("/weekly-activity")
    public ApiResponse<List<WeeklyActivityResponse>>
    getWeeklyActivity() {

        return ResponseFactory.success(
                adminDashboardService.getWeeklyActivity()
        );
    }

    @GetMapping("/monthly-user-growth")
    public ApiResponse<List<MonthlyUserGrowthResponse>>
    getMonthlyUserGrowth(
            @RequestParam(required = false) Integer year
    ) {

        return ResponseFactory.success(
                adminDashboardService.getMonthlyUserGrowth(year)
        );
    }
}