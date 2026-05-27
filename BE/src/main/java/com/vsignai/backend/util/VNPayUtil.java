package com.vsignai.backend.util;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

@UtilityClass
public class VNPayUtil {

    public String hmacSHA512(String key, String data) {

        try {

            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            key.getBytes(),
                            "HmacSHA512"
                    );

            hmac512.init(secretKey);

            byte[] bytes =
                    hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA512");
        }
    }

    public String buildQueryUrl(Map<String, String> params) {

        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {

            if (entry.getValue() != null
                    && !entry.getValue().isBlank()) {

                query.append(
                        URLEncoder.encode(
                                entry.getKey(),
                                StandardCharsets.UTF_8
                        )
                );

                query.append("=");

                query.append(
                        URLEncoder.encode(
                                entry.getValue(),
                                StandardCharsets.UTF_8
                        )
                );

                query.append("&");
            }
        }

        if (!query.isEmpty()) {
            query.setLength(query.length() - 1);
        }

        return query.toString();
    }

    public TreeMap<String, String> buildSortedMap() {
        return new TreeMap<>();
    }

    public String buildHashData(Map<String, String> params) {

        TreeMap<String, String> sorted =
                new TreeMap<>(params);

        sorted.remove("vnp_SecureHash");
        sorted.remove("vnp_SecureHashType");

        return buildQueryUrl(sorted);
    }
}