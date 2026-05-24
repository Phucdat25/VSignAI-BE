package com.vsignai.backend.util.payment;

import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.enums.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentStateManager {

    private final PaymentValidator paymentValidator;

    public void markProcessing(Payment payment) {
        transition(payment, PaymentStatus.PROCESSING);
    }

    public void markSuccess(Payment payment) {
        transition(payment, PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
    }

    public void markFailed(Payment payment) {
        transition(payment, PaymentStatus.FAILED);
    }

    public void markCanceled(Payment payment) {
        transition(payment, PaymentStatus.CANCELED);
    }

    private void transition(Payment payment, PaymentStatus targetStatus) {
        paymentValidator.validateStatusTransition(
                payment.getStatus(),
                targetStatus
        );
        payment.setStatus(targetStatus);
    }
}
