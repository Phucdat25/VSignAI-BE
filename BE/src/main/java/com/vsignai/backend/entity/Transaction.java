package com.vsignai.backend.entity;


import com.vsignai.backend.enums.transaction.TransactionStatus;
import com.vsignai.backend.enums.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private String provider;
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(columnDefinition = "json")
    private String rawResponse;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
