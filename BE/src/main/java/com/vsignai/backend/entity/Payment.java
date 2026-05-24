package com.vsignai.backend.entity;

import com.vsignai.backend.enums.payment.PaymentGateway;
import com.vsignai.backend.enums.payment.PaymentMethod;
import com.vsignai.backend.enums.payment.PaymentStatus;
import com.vsignai.backend.enums.payment.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_subscription", columnList = "subscription_id"),
                @Index(name = "idx_payment_idempotency", columnList = "idempotency_key"),
                @Index(name = "idx_payment_gateway_tx", columnList = "gateway_transaction_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_transaction_id",
                        columnNames = "transaction_id"
                ),
                @UniqueConstraint(
                        name = "uq_payment_idempotency",
                        columnNames = "idempotency_key"
                ),
                @UniqueConstraint(
                        name = "uq_payment_gateway_tx",
                        columnNames = "gateway_transaction_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    // internal tracking id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    // anti duplicate request
    @Column(name = "idempotency_key")
    private String idempotencyKey;

    // gateway reference (VNPay / Stripe / PayOS)
    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String planCode;

    @Column(nullable = false)
    private Integer billingInterval;

    @Column(nullable = false)
    private String billingUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private LocalDateTime paidAt;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;
}