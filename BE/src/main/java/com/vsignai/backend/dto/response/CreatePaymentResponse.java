package com.vsignai.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreatePaymentResponse {

    private String paymentUrl;

    private String transactionId;

    private LocalDateTime expiresAt;
}
