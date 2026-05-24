package com.vsignai.backend.util.payment;

import com.vsignai.backend.config.VNPayProperties;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.enums.payment.PaymentType;
import com.vsignai.backend.util.subscription.BillingPeriodCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentFactory {

    private final BillingPeriodCalculator billingPeriodCalculator;
    private final VNPayProperties vnPayProperties;

    public Payment createNewSubscriptionPayment(
            UserSubscription sub,
            PaymentMethod method,
            PaymentGateway gateway
    ) {
        return buildPayment(
                sub,
                method,
                gateway,
                PaymentType.NEW_SUBSCRIPTION,
                LocalDateTime.now()
        );
    }

    public Payment createRenewalPayment(
            UserSubscription sub,
            PaymentMethod method,
            PaymentGateway gateway
    ) {
        LocalDateTime start =
                sub.getCurrentPeriodEnd() != null
                        ? sub.getCurrentPeriodEnd()
                        : LocalDateTime.now();

        return buildPayment(
                sub,
                method,
                gateway,
                PaymentType.RENEWAL,
                start
        );
    }

    private Payment buildPayment(
            UserSubscription sub,
            PaymentMethod method,
            PaymentGateway gateway,
            PaymentType type,
            LocalDateTime periodStart
    ) {
        LocalDateTime now = LocalDateTime.now();
        String gatewayRef = generateGatewayRef();

        return Payment.builder()
                .subscription(sub)
                .amount(sub.getPlan().getPrice())
                .currency(sub.getPlan().getCurrency())
                .status(PaymentStatus.PENDING)
                .type(type)
                .paymentMethod(method)
                .gateway(gateway)
                .transactionId(UUID.randomUUID().toString())
                .gatewayTransactionId(gatewayRef)
                .idempotencyKey(buildIdempotencyKey(sub.getId(), type, gatewayRef))
                .expiresAt(now.plusMinutes(vnPayProperties.getExpireMinutes()))
                .periodStart(periodStart)
                .periodEnd(
                        billingPeriodCalculator.calculatePeriodEnd(
                                periodStart,
                                sub.getPlan()
                        )
                )
                .planName(sub.getPlan().getName())
                .planCode(sub.getPlan().getCode().name())
                .billingInterval(sub.getPlan().getIntervalCount())
                .billingUnit(
                        sub.getPlan()
                                .getIntervalUnit()
                                .name()
                )
                .build();
    }

    private String buildIdempotencyKey(
            Long subscriptionId,
            PaymentType type,
            String gatewayRef
    ) {
        return "sub-" + subscriptionId + "-" + type.name() + "-" + gatewayRef;
    }

    private String generateGatewayRef() {
        return "PAY_"
                + System.currentTimeMillis()
                + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
