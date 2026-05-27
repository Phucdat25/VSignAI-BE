package com.vsignai.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateSubscriptionResponse {

    private String paymentUrl;

    private String transactionId;

    private LocalDateTime expiresAt;
}