package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.PaymentInitRequest;
import com.vsignai.backend.dto.response.PaymentInitResponse;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.enums.payment.PaymentType;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.PaymentService;
import com.vsignai.backend.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final VNPayService vnPayService;

    @Override
    public PaymentInitResponse initiatePayment(
            PaymentInitRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        if (request.getPackageType() == null || request.getPackageType().isBlank()) {
            throw new RuntimeException("Package type is required");
        }

        String packageType = request.getPackageType().toUpperCase();

        BigDecimal amount;
        String planName;
        Integer billingInterval;
        String billingUnit;

        switch (packageType) {
            case "PRO_MONTH" -> {
                amount = new BigDecimal("79000");
                planName = "Pro Monthly";
                billingInterval = 1;
                billingUnit = "MONTH";
            }
            case "PRO_YEAR" -> {
                amount = new BigDecimal("799000");
                planName = "Pro Yearly";
                billingInterval = 1;
                billingUnit = "YEAR";
            }
            default -> throw new RuntimeException("Invalid package type");
        }

        User user;

        Object principal = authentication.getPrincipal();

        if (principal instanceof User currentUser) {
            user = currentUser;
        } else {
            String email = authentication.getName();

            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime periodStart = now;

        LocalDateTime periodEnd = "MONTH".equals(billingUnit)
                ? now.plusMonths(billingInterval)
                : now.plusYears(billingInterval);

        String transactionId = "VNPAY" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .user(user)
                .transactionId(transactionId)
                .idempotencyKey(UUID.randomUUID().toString())

                .amount(amount)
                .currency("VND")

                .planName(planName)
                .planCode(packageType)

                .billingInterval(billingInterval)
                .billingUnit(billingUnit)

                .status(PaymentStatus.PENDING)
                .type(PaymentType.NEW_SUBSCRIPTION)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .gateway(PaymentGateway.VNPAY)

                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .expiresAt(now.plusMinutes(15))

                .build();

        Payment savedPayment = paymentRepository.save(payment);

        String paymentUrl = vnPayService.createPaymentUrl(savedPayment, httpRequest);

        return new PaymentInitResponse(paymentUrl, transactionId);
    }
}