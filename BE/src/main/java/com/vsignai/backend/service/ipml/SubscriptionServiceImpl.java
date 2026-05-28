package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.CreateSubscriptionRequest;
import com.vsignai.backend.dto.response.CreateSubscriptionResponse;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.enums.payment.PaymentType;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.exception.BusinessException;
import com.vsignai.backend.exception.NotFoundException;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.repository.SubscriptionPlanRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.SubscriptionService;
import com.vsignai.backend.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;

    private final SubscriptionPlanRepository planRepository;

    private final PaymentRepository paymentRepository;

    private final VNPayService vnPayService;

    @Override
    public UserSubscription getActiveSubscription(User user) {

        return subscriptionRepository
                .findCurrentActiveSubscription(user.getId())
                .orElse(null);
    }

    @Override
    @Transactional
    public CreateSubscriptionResponse createSubscription(
            User user,
            CreateSubscriptionRequest request,
            String idempotencyKey,
            HttpServletRequest httpRequest
    ) {

        // =====================================================
        // STEP 1 — VALIDATE USER
        // =====================================================

        if (user == null) {
            throw new BusinessException("User not authenticated");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User inactive");
        }

        // =====================================================
        // STEP 2 — IDEMPOTENCY CHECK
        // =====================================================

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {

            Optional<Payment> existingPayment =
                    paymentRepository.findByIdempotencyKey(idempotencyKey);

            if (existingPayment.isPresent()) {

                Payment payment = existingPayment.get();

                String paymentUrl =
                        vnPayService.createPaymentUrl(payment, httpRequest);

                return CreateSubscriptionResponse.builder()
                        .paymentUrl(paymentUrl)
                        .transactionId(payment.getTransactionId())
                        .expiresAt(payment.getExpiresAt())
                        .build();
            }
        }

        // =====================================================
        // STEP 3 — LOAD PLAN
        // =====================================================

        SubscriptionPlan plan =
                planRepository.findByCode(request.getPlanCode())
                        .orElseThrow(() ->
                                new NotFoundException("Plan not found")
                        );

        // =====================================================
        // STEP 4 — VALIDATE PLAN
        // =====================================================

        if (!plan.getIsActive()) {
            throw new BusinessException("Plan inactive");
        }

        if (plan.getCode() == PlanCode.FREE) {
            throw new BusinessException(
                    "Free plan does not require payment"
            );
        }

        if (!"VND".equals(plan.getCurrency())) {
            throw new BusinessException(
                    "VNPay only supports VND currently"
            );
        }

        // =====================================================
        // STEP 5 — CHECK ACTIVE SUBSCRIPTION
        // =====================================================

        Optional<UserSubscription> activeSubscription =
                subscriptionRepository
                        .findCurrentActiveSubscription(user.getId());

        if (activeSubscription.isPresent()) {

            throw new BusinessException(
                    "User already has active subscription"
            );
        }

        // =====================================================
        // STEP 6 — CHECK PENDING PAYMENT
        // =====================================================

        Optional<Payment> pendingPayment =
                paymentRepository
                        .findTopBySubscription_User_IdAndStatusAndExpiresAtAfterOrderByCreatedAtDesc(
                                user.getId(),
                                PaymentStatus.PENDING,
                                LocalDateTime.now()
                        );

        if (pendingPayment.isPresent()) {

            Payment payment = pendingPayment.get();

            String paymentUrl =
                    vnPayService.createPaymentUrl(payment, httpRequest);

            return CreateSubscriptionResponse.builder()
                    .paymentUrl(paymentUrl)
                    .transactionId(payment.getTransactionId())
                    .expiresAt(payment.getExpiresAt())
                    .build();
        }

        // =====================================================
        // STEP 7 — CREATE SUBSCRIPTION
        // =====================================================

        UserSubscription subscription =
                UserSubscription.builder()
                        .user(user)
                        .plan(plan)
                        .status(SubscriptionStatus.PENDING)
                        .isAutoRenew(false)
                        .currentPeriodStart(LocalDateTime.now())
                        .currentPeriodEnd(LocalDateTime.now())
                        .build();

        subscriptionRepository.save(subscription);

        // =====================================================
        // STEP 8 — CREATE PAYMENT
        // =====================================================

        Payment payment =
                Payment.builder()
                        .subscription(subscription)
                        .transactionId(UUID.randomUUID().toString())
                        .idempotencyKey(idempotencyKey)
                        .status(PaymentStatus.PENDING)
                        .gateway(PaymentGateway.VNPAY)
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .type(PaymentType.NEW_SUBSCRIPTION)
                        .amount(plan.getPrice())
                        .currency(plan.getCurrency())
                        .planCode(plan.getCode().name())
                        .planName(plan.getName())
                        .billingInterval(plan.getIntervalCount())
                        .billingUnit(plan.getIntervalUnit().name())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .build();

        paymentRepository.save(payment);

        // =====================================================
        // STEP 9 — GENERATE PAYMENT URL
        // =====================================================

        String paymentUrl =
                vnPayService.createPaymentUrl(payment, httpRequest);

        // =====================================================
        // STEP 10 — RESPONSE
        // =====================================================

        return CreateSubscriptionResponse.builder()
                .paymentUrl(paymentUrl)
                .transactionId(payment.getTransactionId())
                .expiresAt(payment.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public void activateSubscription(Payment payment) {

        if (payment.getStatus() == PaymentStatus.SUCCESS
                && payment.getSubscription() != null
                && payment.getSubscription().getStatus() == SubscriptionStatus.ACTIVE) {
            return;
        }

        User user = payment.getUser();

        // 1. Expire tất cả subscription ACTIVE cũ của user
        List<UserSubscription> activeSubscriptions =
                subscriptionRepository.findAllByUserAndStatus(
                        user,
                        SubscriptionStatus.ACTIVE
                );

        for (UserSubscription active : activeSubscriptions) {
            active.setStatus(SubscriptionStatus.EXPIRED);
            active.setCanceledAt(LocalDateTime.now());
            subscriptionRepository.save(active);
        }

        // 2. Lấy plan từ payment
        SubscriptionPlan plan =
                planRepository.findByCode(
                        PlanCode.valueOf(payment.getPlanCode())
                ).orElseThrow(() ->
                        new NotFoundException("Plan not found")
                );

        // 3. Tạo subscription mới
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime periodEnd;

        if ("MONTH".equals(payment.getBillingUnit())) {
            periodEnd = now.plusMonths(payment.getBillingInterval());
        } else if ("YEAR".equals(payment.getBillingUnit())) {
            periodEnd = now.plusYears(payment.getBillingInterval());
        } else {
            throw new BusinessException("Invalid billing unit");
        }

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .isAutoRenew(false)
                .startedAt(now)
                .currentPeriodStart(now)
                .currentPeriodEnd(periodEnd)
                .build();

        UserSubscription savedSubscription =
                subscriptionRepository.save(subscription);

        // 4. Gắn subscription mới vào payment
        payment.setSubscription(savedSubscription);
        paymentRepository.save(payment);
    }
}
//    public void activateSubscription(Payment payment) {
//
//        UserSubscription subscription =
//                payment.getSubscription();
//
//        // =====================================================
//        // CREATE SUBSCRIPTION IF NULL
//        // =====================================================
//
//        if (subscription == null) {
//
//            SubscriptionPlan plan =
//                    planRepository.findByCode(
//                            PlanCode.valueOf(payment.getPlanCode())
//                    ).orElseThrow(() ->
//                            new NotFoundException("Plan not found")
//                    );
//
//            subscription = UserSubscription.builder()
//                    .user(payment.getUser())
//                    .plan(plan)
//                    .status(SubscriptionStatus.PENDING)
//                    .isAutoRenew(false)
//                    .build();
//
//            subscriptionRepository.save(subscription);
//
//            payment.setSubscription(subscription);
//
//            paymentRepository.save(payment);
//        }
//
//        // =====================================================
//        // ALREADY ACTIVE
//        // =====================================================
//
//        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
//            return;
//        }
//
//        // =====================================================
//        // ACTIVATE
//        // =====================================================
//
//        LocalDateTime now = LocalDateTime.now();
//
//        subscription.setStatus(SubscriptionStatus.ACTIVE);
//
//        subscription.setStartedAt(now);
//
//        subscription.setCurrentPeriodStart(now);
//
//        if ("MONTH".equals(payment.getBillingUnit())) {
//
//            subscription.setCurrentPeriodEnd(
//                    now.plusMonths(payment.getBillingInterval())
//            );
//
//        } else if ("YEAR".equals(payment.getBillingUnit())) {
//
//            subscription.setCurrentPeriodEnd(
//                    now.plusYears(payment.getBillingInterval())
//            );
//        }
//
//        subscriptionRepository.save(subscription);
//    }
