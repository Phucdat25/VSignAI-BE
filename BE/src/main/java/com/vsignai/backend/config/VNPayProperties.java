package com.vsignai.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment.vnpay")
public class VNPayProperties {

    private String tmnCode;

    private String hashSecret;

    private String secret;

    private String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    /** Sent to VNPay as vnp_ReturnUrl — should hit this backend to confirm payment. */
    private String returnUrl;

    /** After backend confirms, redirect user to this frontend page. */
    private String frontendRedirectUrl;

    private String version = "2.1.0";

    private String command = "pay";

    private String orderType = "other";

    private String locale = "vn";

    /**
     * Registered in VNPay merchant portal (server-to-server IPN).
     */
    private String ipnUrl;

    private int expireMinutes = 15;

    public void setTmnCode(String tmnCode) {
        this.tmnCode = trim(tmnCode);
    }

    public void setHashSecret(String hashSecret) {
        this.hashSecret = trim(hashSecret);
    }

    public void setSecret(String secret) {
        this.secret = trim(secret);
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = trim(returnUrl);
    }

    public void setFrontendRedirectUrl(String frontendRedirectUrl) {
        this.frontendRedirectUrl = trim(frontendRedirectUrl);
    }

    public String getEffectiveHashSecret() {
        if (hashSecret != null && !hashSecret.isBlank()) {
            return hashSecret;
        }
        return secret;
    }

    public boolean isConfigured() {
        return tmnCode != null
                && !tmnCode.isBlank()
                && getEffectiveHashSecret() != null
                && !getEffectiveHashSecret().isBlank();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
