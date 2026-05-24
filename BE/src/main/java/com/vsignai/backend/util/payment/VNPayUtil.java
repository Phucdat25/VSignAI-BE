package com.vsignai.backend.util.payment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * VNPay signing rules (official docs):
 * - Payment URL hash: {@code fieldName=URLEncode(value)} (field name NOT encoded)
 * - IPN / return hash: {@code URLEncode(fieldName)=URLEncode(value)}
 */
public final class VNPayUtil {

    private static final String HMAC_SHA512 = "HmacSHA512";

    private VNPayUtil() {
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            key.getBytes(StandardCharsets.UTF_8),
                            HMAC_SHA512
                    );
            mac.init(secretKey);

            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hash.append(String.format("%02x", b & 0xff));
            }

            return hash.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign VNPay data", ex);
        }
    }

    public static boolean secureEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return left.equalsIgnoreCase(right);
    }

    /** Hash input when creating payment redirect URL. */
    public static String buildHashDataForPayment(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : sortedSecureParams(params).entrySet()) {
            if (!first) {
                hashData.append('&');
            }
            hashData.append(entry.getKey());
            hashData.append('=');
            hashData.append(encode(entry.getValue()));
            first = false;
        }

        return hashData.toString();
    }

    /** Query string when creating payment redirect URL. */
    public static String buildQueryForPayment(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : sortedSecureParams(params).entrySet()) {
            if (!first) {
                query.append('&');
            }
            query.append(encode(entry.getKey()));
            query.append('=');
            query.append(encode(entry.getValue()));
            first = false;
        }

        return query.toString();
    }

    /** Hash input when verifying IPN / return callback (PHP sample). */
    public static String buildHashDataForIpn(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : sortedSecureParams(params).entrySet()) {
            if (!first) {
                hashData.append('&');
            }
            hashData.append(encode(entry.getKey()));
            hashData.append('=');
            hashData.append(encode(entry.getValue()));
            first = false;
        }

        return hashData.toString();
    }

    private static Map<String, String> sortedSecureParams(
            Map<String, String> params
    ) {
        Map<String, String> sorted = new TreeMap<>();

        params.forEach((key, value) -> {
            if (value == null || value.isBlank()) {
                return;
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                return;
            }
            sorted.put(key, value);
        });

        return sorted;
    }

    /**
     * VNPay samples use {@link StandardCharsets#US_ASCII} and keep '+' for spaces.
     */
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}
