package com.vsignai.backend.util.payment;

import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final PaymentRepository paymentRepository;

    public void validateNoPendingPayment(Long subscriptionId) {

        paymentRepository
                .findActivePendingBySubscriptionId(
                        subscriptionId,
                        PaymentStatus.PENDING,
                        LocalDateTime.now()
                )
                .ifPresent(payment -> {
                    throw new AppException(
                            "PAYMENT_ALREADY_PENDING",
                            "Pending payment already exists",
                            HttpStatus.BAD_REQUEST
                    );
                });
    }

    public void validatePaymentNotFinal(Payment payment) {

        if (isFinalStatus(payment.getStatus())) {
            throw new AppException(
                    "PAYMENT_ALREADY_FINALIZED",
                    "Payment already finalized",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public boolean isFinalStatus(PaymentStatus status) {
        return status == PaymentStatus.SUCCESS
                || status == PaymentStatus.FAILED
                || status == PaymentStatus.CANCELED
                || status == PaymentStatus.REFUNDED;
    }

    public void validateStatusTransition(
            PaymentStatus currentStatus,
            PaymentStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {

            case PENDING -> newStatus == PaymentStatus.SUCCESS
                    || newStatus == PaymentStatus.FAILED
                    || newStatus == PaymentStatus.PROCESSING
                    || newStatus == PaymentStatus.CANCELED;

            case PROCESSING -> newStatus == PaymentStatus.SUCCESS
                    || newStatus == PaymentStatus.FAILED
                    || newStatus == PaymentStatus.CANCELED;

            case SUCCESS -> newStatus == PaymentStatus.REFUNDED;

            case FAILED, CANCELED, REFUNDED -> false;
        };

        if (!valid) {
            throw new AppException(
                    "INVALID_PAYMENT_STATUS_TRANSITION",
                    "Cannot transition payment status from "
                            + currentStatus
                            + " to "
                            + newStatus,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public boolean isExpired(Payment payment) {
        return payment.getExpiresAt() != null
                && payment.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
