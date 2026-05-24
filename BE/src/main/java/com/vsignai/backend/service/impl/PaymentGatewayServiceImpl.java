package com.vsignai.backend.service.impl;

import com.vsignai.backend.dto.VNPayWebhookRequest;
import com.vsignai.backend.service.PaymentGatewayService;
import com.vsignai.backend.util.payment.VNPayUtil;
import com.vsignai.backend.config.VNPayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGatewayServiceImpl
        implements PaymentGatewayService {

    private final VNPayProperties vnPayProperties;

    @Override
    public boolean verifySignature(
            VNPayWebhookRequest request,
            String signature
    ) {

        if (signature == null || signature.isBlank()) {
            return false;
        }

        String rawData = request.toRawString();

        String expectedSignature =
                VNPayUtil.hmacSHA512(
                        vnPayProperties.getEffectiveHashSecret(),
                        rawData
                );

        return VNPayUtil.secureEquals(expectedSignature, signature);
    }
}
