package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.response.*;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.PaymentStatus;
import com.vsignai.backend.enums.RevenueFilterType;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.repository.UsageLogRepository;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final UsageLogRepository usageLogRepository;

    //Tính tổng user, user premiun, user họt động theo ngày
    @Override
    public AdminUserStatisticsResponse getStatistics() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();

        return AdminUserStatisticsResponse.builder()
                .totalUsers(userRepository.countByDeletedAtIsNull())
                .premiumUsers(
                        userSubscriptionRepository.countByPlan_CodeInAndStatus(
                                List.of(PlanCode.PRO_MONTH, PlanCode.PRO_YEAR),
                                SubscriptionStatus.ACTIVE
                        )
                )
                .activeToday(usageLogRepository.countActiveUsersToday(startOfToday))
                .build();
    }

    //lấy toàn bộ user
    @Override
    public Page<AdminUserResponse> getUsers(int page, int size) {
        return userRepository.findByDeletedAtIsNull(PageRequest.of(page, size))
                .map(this::mapToAdminUserResponse);
    }

    //lấy thông tin chi tiết của 1 user theo ID
    @Override
    public AdminUserResponse getUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"));

        return mapToAdminUserResponse(user);
    }

    //cập nhật status của user
    @Override
    public void updateUserStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"));

        user.setStatus(status);
        userRepository.save(user);
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        UserSubscription subscription = userSubscriptionRepository
                .findTopByUserIdAndStatusAndPlan_CodeInOrderByCreatedAtDesc(
                        user.getId(),
                        SubscriptionStatus.ACTIVE,
                        PREMIUM_PLANS
                )
                .orElse(null);
        System.out.println("USER ID = " + user.getId());
        System.out.println("SUBSCRIPTION = " + subscription);
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .planCode(subscription != null ? subscription.getPlan().getCode().name() : "FREE")
                .planName(subscription != null ? subscription.getPlan().getName() : "Miễn phí")
                .joinedAt(user.getCreatedAt())
                .build();
    }
    private static final List<PlanCode> PREMIUM_PLANS =
            List.of(PlanCode.PRO_MONTH, PlanCode.PRO_YEAR);

    //lấy thông tin cho trang dashboard admin
    @Override
    public AdminDashboardOverviewResponse getOverview() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfMonth = now
                .toLocalDate()
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        return AdminDashboardOverviewResponse.builder()
                .monthlyRevenue(
                        paymentRepository.getMonthlyRevenue(
                                startOfMonth,
                                startOfNextMonth
                        )
                )
                .totalUsers(userRepository.countByDeletedAtIsNull())
                .totalTranslations(usageLogRepository.count())
                .premiumUsers(
                        userSubscriptionRepository.countByPlan_CodeInAndStatus(
                                PREMIUM_PLANS,
                                SubscriptionStatus.ACTIVE
                        )
                )
                .dailyTranslations(getRealDailyTranslations())
                .monthlyUserGrowth(getRealMonthlyUserGrowth())
                .build();
    }

    //lấy sô lần dịch mỗi  ngày
    private List<ChartPointResponse> getRealDailyTranslations() {

        LocalDateTime start = LocalDateTime.now()
                .minusDays(6)
                .toLocalDate()
                .atStartOfDay();

        return usageLogRepository.countDailyUsage(start)
                .stream()
                .map(row -> ChartPointResponse.builder()
                        .label(row[0].toString())
                        .value((Long) row[1])
                        .build())
                .toList();
    }

    //lấy tổng user theo tháng
    private List<ChartPointResponse> getRealMonthlyUserGrowth() {

        LocalDateTime start = LocalDateTime.now()
                .minusMonths(5)
                .toLocalDate()
                .withDayOfMonth(1)
                .atStartOfDay();

        return userRepository.countMonthlyUsers(start)
                .stream()
                .map(row -> ChartPointResponse.builder()
                        .label("T" + row[0])
                        .value((Long) row[1])
                        .build())
                .toList();
    }

    //doanh thu theo ngày tháng năm có filter
    @Override
    public RevenueResponse getRevenue(RevenueFilterType type, Integer year, Integer month, LocalDate date) {
        LocalDateTime start;
        LocalDateTime end;
        String label;

        if (type == RevenueFilterType.DAY) {
            LocalDate targetDate = date != null ? date : LocalDate.now();

            start = targetDate.atStartOfDay();
            end = targetDate.plusDays(1).atStartOfDay();
            label = targetDate.toString();
        } else if (type == RevenueFilterType.MONTH) {
            int targetYear = year != null ? year : LocalDate.now().getYear();
            int targetMonth = month != null ? month : LocalDate.now().getMonthValue();

            LocalDate startDate = LocalDate.of(targetYear, targetMonth, 1);

            start = startDate.atStartOfDay();
            end = startDate.plusMonths(1).atStartOfDay();
            label = targetMonth + "/" + targetYear;
        } else {
            int targetYear = year != null ? year : LocalDate.now().getYear();

            LocalDate startDate = LocalDate.of(targetYear, 1, 1);

            start = startDate.atStartOfDay();
            end = startDate.plusYears(1).atStartOfDay();
            label = String.valueOf(targetYear);
        }

        BigDecimal revenue = paymentRepository.sumRevenue(
                PaymentStatus.SUCCESS,
                start,
                end
        );

        return RevenueResponse.builder()
                .type(type.name())
                .label(label)
                .revenue(revenue)
                .build();
    }

    //tính tỉ lệ chuyển đổi theo tháng
    @Override
    public List<ConversionRatePointResponse> getMonthlyConversionRate(
            Integer year
    ) {

        int targetYear = year != null
                ? year
                : LocalDate.now().getYear();

        List<Object[]> totalUsers =
                userRepository.countUsersByMonth(targetYear);

        List<Object[]> premiumUsers =
                userSubscriptionRepository.countPremiumUsersByMonth(
                        targetYear,
                        PREMIUM_PLANS
                );

        return totalUsers.stream()
                .map(row -> {

                    Integer month = (Integer) row[0];
                    Long total = (Long) row[1];

                    Long premium = premiumUsers.stream()
                            .filter(p -> p[0].equals(month))
                            .map(p -> (Long) p[1])
                            .findFirst()
                            .orElse(0L);

                    double conversionRate =
                            total == 0
                                    ? 0
                                    : (premium * 100.0 / total);

                    return ConversionRatePointResponse.builder()
                            .label("T" + month)
                            .totalUsers(total)
                            .premiumUsers(premium)
                            .conversionRate(
                                    Math.round(conversionRate * 100.0) / 100.0
                            )
                            .build();
                })
                .toList();
    }

    //biểu đồ user và số lượt dịch
    @Override
    public List<WeeklyActivityResponse> getWeeklyActivity() {
        LocalDate today = LocalDate.now();

        LocalDate startDate = today.minusDays(6);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Map<LocalDate, Object[]> dataMap = usageLogRepository
                .getWeeklyActivity(start, end)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> row
                ));

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate date = startDate.plusDays(i);
                    Object[] row = dataMap.get(date);

                    return WeeklyActivityResponse.builder()
                            .label(toVietnameseDayLabel(date))
                            .activeUsers(row != null ? (Long) row[1] : 0L)
                            .translations(row != null ? (Long) row[2] : 0L)
                            .build();
                })
                .toList();
    }

    private String toVietnameseDayLabel(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
    @Override
    public List<MonthlyUserGrowthResponse> getMonthlyUserGrowth(Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();

        Map<Integer, Long> totalMap = userRepository.countUsersByMonth(targetYear)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        Map<Integer, Long> premiumMap = userSubscriptionRepository
                .countPremiumUsersByMonth(
                        targetYear,
                        PREMIUM_PLANS
                )
                .stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> MonthlyUserGrowthResponse.builder()
                        .label("T" + month)
                        .totalUsers(totalMap.getOrDefault(month, 0L))
                        .premiumUsers(premiumMap.getOrDefault(month, 0L))
                        .build())
                .toList();
    }
}
