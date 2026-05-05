package com.vsignai.backend.entity;


import com.vsignai.backend.enums.transaction.TransactionStatus;
import com.vsignai.backend.enums.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        indexes = @Index(name = "idx_transaction_payment", columnList = "payment_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(columnDefinition = "json")
    private String rawResponse;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
