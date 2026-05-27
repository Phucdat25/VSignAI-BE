package com.vsignai.backend.service.ipml;

import com.vsignai.backend.config.VNPayConfig;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.service.VNPayService;
import com.vsignai.backend.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;

    @Override
    public String createPaymentUrl(
            Payment payment,
            HttpServletRequest request
    ) {

        TreeMap<String, String> params =
                VNPayUtil.buildSortedMap();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());

        params.put(
                "vnp_Amount",
                payment.getAmount()
                        .multiply(java.math.BigDecimal.valueOf(100))
                        .toBigInteger()
                        .toString()
        );

        params.put("vnp_CurrCode", "VND");

        params.put(
                "vnp_TxnRef",
                payment.getTransactionId()
        );

        params.put(
                "vnp_OrderInfo",
                "Payment for " + payment.getPlanName()
        );

        params.put(
                "vnp_OrderType",
                "other"
        );

        params.put(
                "vnp_Locale",
                "vn"
        );

        params.put(
                "vnp_ReturnUrl",
                vnPayConfig.getReturnUrl()
        );


        params.put(
                "vnp_IpAddr",
                request.getRemoteAddr()
        );

        Calendar calendar =
                Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyyMMddHHmmss");

        String createDate =
                formatter.format(calendar.getTime());

        params.put("vnp_CreateDate", createDate);

        calendar.add(Calendar.MINUTE, 15);

        String expireDate =
                formatter.format(calendar.getTime());

        params.put("vnp_ExpireDate", expireDate);

        String hashData =
                VNPayUtil.buildQueryUrl(params);

        String secureHash =
                VNPayUtil.hmacSHA512(
                        vnPayConfig.getHashSecret(),
                        hashData
                );

        params.put("vnp_SecureHash", secureHash);
        System.out.println(
                "IPN URL = " + vnPayConfig.getIpnUrl()
        );

        String paymentUrl =
                vnPayConfig.getPayUrl()
                        + "?"
                        + VNPayUtil.buildQueryUrl(params);

        System.out.println(
                "PAYMENT URL = " + paymentUrl
        );

        return paymentUrl;
    }

    @Override
    public boolean validateSignature(
            java.util.Map<String, String> params
    ) {

        String secureHash =
                params.get("vnp_SecureHash");

        String hashData =
                VNPayUtil.buildHashData(params);

        String calculatedHash =
                VNPayUtil.hmacSHA512(
                        vnPayConfig.getHashSecret(),
                        hashData
                );

        return calculatedHash.equals(secureHash);
    }
}