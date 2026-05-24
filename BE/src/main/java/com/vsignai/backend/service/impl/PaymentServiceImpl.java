package com.vsignai.backend.service.impl;

import com.vsignai.backend.config.RequestContext;
import com.vsignai.backend.dto.*;
import com.vsignai.backend.entity.Payment;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.mapper.PaymentMapper;
import com.vsignai.backend.repository.PaymentRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.PaymentGatewayService;
import com.vsignai.backend.service.PaymentService;
import com.vsignai.backend.service.VNPayGatewayService;
import com.vsignai.backend.service.payment.PaymentCompletionService;
import com.vsignai.backend.service.payment.VNPayIpnService;
import com.vsignai.backend.util.payment.PaymentFactory;
import com.vsignai.backend.util.payment.PaymentValidator;
import com.vsignai.backend.util.subscription.SubscriptionValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final VNPayGatewayService vnPayGatewayService;
    private final PaymentValidator paymentValidator;
    private final SubscriptionValidator subscriptionValidator;
    private final PaymentFactory paymentFactory;
    private final VNPayIpnService vnPayIpnService;
    private final PaymentCompletionService paymentCompletionService;

    @Override
    @Transactional
    public PaymentResponse create(
            Long userId,
            Long subscriptionId,
            CreatePaymentRequest request
    ) {

        UserSubscription sub = loadSubscription(userId, subscriptionId);
        subscriptionValidator.validatePayable(sub);

        Optional<Payment> reusable =
                paymentRepository.findActivePendingBySubscriptionId(
                        subscriptionId,
                        PaymentStatus.PENDING,
                        LocalDateTime.now()
                );

        if (reusable.isPresent()) {
            log.info(
                    "Reusing pending payment | requestId={} | paymentId={} | subscriptionId={}",
                    RequestContext.getRequestId(),
                    reusable.get().getId(),
                    subscriptionId
            );
            return toPaymentResponse(reusable.get(), request.getPaymentGateway());
        }

        paymentValidator.validateNoPendingPayment(subscriptionId);
        validateGatewaySupported(request.getPaymentGateway());

        Payment payment =
                paymentFactory.createNewSubscriptionPayment(
                        sub,
                        request.getPaymentMethod(),
                        request.getPaymentGateway()
                );

        paymentRepository.save(payment);

        log.info(
                "Payment created | requestId={} | paymentId={} | gatewayRef={} | expiresAt={}",
                RequestContext.getRequestId(),
                payment.getId(),
                payment.getGatewayTransactionId(),
                payment.getExpiresAt()
        );

        return toPaymentResponse(payment, request.getPaymentGateway());
    }

    @Override
    public List<PaymentResponse> getBySubscription(
            Long userId,
            Long subscriptionId
    ) {

        loadSubscription(userId, subscriptionId);

        return paymentRepository
                .findBySubscription_IdOrderByCreatedAtDesc(subscriptionId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    /*@Override
    @Transactional
    public void handleGatewayCallback(
            VNPayWebhookRequest request,
            String signature
    ) {

        if (!paymentGatewayService.verifySignature(request, signature)) {
            throw new AppException(
                    "INVALID_SIGNATURE",
                    "Invalid gateway signature",
                    HttpStatus.BAD_REQUEST
            );
        }

        Payment payment =
                paymentRepository
                        .findByGatewayTransactionIdForUpdate(
                                request.getTransactionId()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        "PAYMENT_NOT_FOUND",
                                        "Payment not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        paymentValidator.validatePaymentNotFinal(payment);

        if (paymentValidator.isExpired(payment)) {
            throw new AppException(
                    "PAYMENT_EXPIRED",
                    "Payment session has expired",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!"SUCCESS".equalsIgnoreCase(request.getStatus())) {
            paymentCompletionService.completeFailure(payment);
            return;
        }

        paymentCompletionService.completeSuccess(payment);
    }*/

    @Override
    @Transactional
    public PaymentResponse renew(
            Long userId,
            Long subscriptionId
    ) {

        UserSubscription sub = loadSubscription(userId, subscriptionId);

        Optional<Payment> reusable =
                paymentRepository.findActivePendingBySubscriptionId(
                        subscriptionId,
                        PaymentStatus.PENDING,
                        LocalDateTime.now()
                );

        if (reusable.isPresent()) {
            return toPaymentResponse(reusable.get(), PaymentGateway.VNPAY);
        }

        Payment payment =
                paymentFactory.createRenewalPayment(
                        sub,
                        PaymentMethod.BANK_TRANSFER,
                        PaymentGateway.VNPAY
                );

        paymentRepository.save(payment);

        return toPaymentResponse(payment, PaymentGateway.VNPAY);
    }

    @Override
    public VNPayIPNResponse handleVNPayIPN(HttpServletRequest request) {
        return vnPayIpnService.handle(extractParams(request));
    }

    @Override
    public boolean handleVNPayReturn(Map<String, String> params) {
        return vnPayIpnService.handleReturnCallback(params);
    }

    private UserSubscription loadSubscription(Long userId, Long subscriptionId) {
        return subscriptionRepository
                .findByIdAndUserId(subscriptionId, userId)
                .orElseThrow(() ->
                        new AppException(
                                "SUBSCRIPTION_NOT_FOUND",
                                "Subscription not found",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private PaymentResponse toPaymentResponse(
            Payment payment,
            PaymentGateway gateway
    ) {
        PaymentResponse response = PaymentMapper.toResponse(payment);
        if (gateway == PaymentGateway.VNPAY) {
            response.setPaymentUrl(vnPayGatewayService.createPaymentUrl(payment));
        }
        return response;
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

    private void validateGatewaySupported(PaymentGateway gateway) {
        if (gateway != PaymentGateway.VNPAY) {
            throw new AppException(
                    "PAYMENT_GATEWAY_NOT_SUPPORTED",
                    "Payment gateway is not supported",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(String txnRef) {

        Payment payment =
                paymentRepository
                        .findByGatewayTransactionId(txnRef)
                        .orElseThrow(() ->
                                new AppException(
                                        "PAYMENT_NOT_FOUND",
                                        "Payment not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        return new PaymentStatusResponse(payment.getStatus());
    }
}
