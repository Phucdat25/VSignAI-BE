package com.vsignai.backend.controller;

import com.vsignai.backend.config.VNPayProperties;
import com.vsignai.backend.dto.VNPayIPNResponse;
import com.vsignai.backend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentVNPayController {

    private final PaymentService paymentService;
    private final VNPayProperties vnPayProperties;

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<VNPayIPNResponse> ipn(HttpServletRequest request) {
        return ResponseEntity.ok(
                paymentService.handleVNPayIPN(request)
        );
    }

    /**
     * VNPay redirects the user's browser here after payment.
     * Updates payment + subscription, then redirects to frontend.
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> vnpayReturn(HttpServletRequest request) {

        Map<String, String> params = extractParams(request);
        boolean processed = paymentService.handleVNPayReturn(params);

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        URI redirect =
                buildFrontendRedirect(processed, responseCode, txnRef);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirect.toString())
                .build();
    }

    private URI buildFrontendRedirect(
            boolean processed,
            String responseCode,
            String txnRef
    ) {
        String base = vnPayProperties.getFrontendRedirectUrl();
        if (base == null || base.isBlank()) {
            base = "http://localhost:5173/payment/result";
        }

        return UriComponentsBuilder
                .fromUriString(base)
                .queryParam("success", processed && "00".equals(responseCode))
                .queryParam("txnRef", txnRef)
                .queryParam("responseCode", responseCode)
                .build()
                .toUri();
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }

        return params;
    }
}
