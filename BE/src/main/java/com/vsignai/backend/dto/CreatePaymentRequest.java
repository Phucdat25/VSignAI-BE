package com.vsignai.backend.dto;

import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CreatePaymentRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment gateway is required")
    private PaymentGateway paymentGateway;
}
