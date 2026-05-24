package com.vsignai.backend.service.impl;

import com.vsignai.backend.config.VNPayProperties;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.service.VNPayGatewayService;
import com.vsignai.backend.util.payment.VNPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VNPayGatewayServiceImpl implements VNPayGatewayService {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VNPayProperties properties;

    @Override
    public String createPaymentUrl(Payment payment) {
        validateConfig();

        String txnRef = payment.getGatewayTransactionId();
        String amount = toVnPayAmount(payment.getAmount());
        String currCode = normalizeCurrency(payment.getCurrency());

        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", properties.getVersion());
        params.put("vnp_Command", properties.getCommand());
        params.put("vnp_TmnCode", properties.getTmnCode());
        params.put("vnp_Amount", amount);
        params.put("vnp_CurrCode", currCode);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan " + txnRef);
        params.put("vnp_OrderType", properties.getOrderType());
        params.put("vnp_Locale", properties.getLocale());
        params.put("vnp_ReturnUrl", properties.getReturnUrl());
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put(
                "vnp_CreateDate",
                LocalDateTime.now().format(VNPAY_DATE_FORMAT)
        );

        String hashData = VNPayUtil.buildHashDataForPayment(params);
        String secureHash =
                VNPayUtil.hmacSHA512(
                        properties.getEffectiveHashSecret(),
                        hashData
                );

        if (log.isDebugEnabled()) {
            log.debug("VNPay pay hashData={}", hashData);
        }

        return properties.getPayUrl()
                + "?"
                + VNPayUtil.buildQueryForPayment(params)
                + "&vnp_SecureHash="
                + secureHash;
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        String hashSecret = properties.getEffectiveHashSecret();

        if (
                secureHash == null
                        || secureHash.isBlank()
                        || hashSecret == null
                        || hashSecret.isBlank()
        ) {
            return false;
        }

        String hashData = VNPayUtil.buildHashDataForIpn(params);
        String expectedHash = VNPayUtil.hmacSHA512(hashSecret, hashData);

        boolean valid = VNPayUtil.secureEquals(expectedHash, secureHash);

        if (!valid) {
            log.warn(
                    "VNPay checksum mismatch | txnRef={} | hashData={}",
                    params.get("vnp_TxnRef"),
                    hashData
            );
        }

        return valid;
    }

    @Override
    public boolean validateMerchant(Map<String, String> params) {
        String tmnCode = params.get("vnp_TmnCode");
        String configured = properties.getTmnCode();

        if (configured == null || configured.isBlank()) {
            return false;
        }

        return configured.equals(tmnCode);
    }

    private String toVnPayAmount(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "VND";
        }
        return currency.trim().toUpperCase();
    }

    private void validateConfig() {
        if (
                isBlank(properties.getTmnCode())
                        || isBlank(properties.getEffectiveHashSecret())
                        || isBlank(properties.getPayUrl())
                        || isBlank(properties.getReturnUrl())
        ) {
            throw new AppException(
                    "VNPAY_CONFIG_MISSING",
                    "VNPay configuration is missing. Set VNPAY_TMN_CODE and VNPAY_HASH_SECRET in Run Configuration.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
