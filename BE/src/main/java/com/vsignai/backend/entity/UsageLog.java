package com.vsignai.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_logs",
        indexes = @Index(name = "idx_usage_user_date", columnList = "user_id, usage_date"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String featureCode;

    private Integer usedValue;

    private LocalDate usageDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
