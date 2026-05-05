package com.vsignai.backend.entity;

import com.vsignai.backend.enums.feature.FeatureCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "usage_logs",
        indexes = {
                @Index(name = "idx_usage_subscription", columnList = "subscription_id"),
                @Index(name = "idx_usage_user_date", columnList = "user_id, usage_date"),
                @Index(name = "idx_feature_only", columnList = "feature_code"),
                @Index(name = "idx_usage_user_feature_date", columnList = "user_id, feature_code, usage_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USER
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // SUBSCRIPTION (quan trọng cho audit)
    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    // FEATURE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeatureCode featureCode;

    // USAGE (rõ ràng đơn vị)
    @Column(nullable = false)
    private Integer usedSeconds;

    // Dùng để query theo ngày
    @Column(nullable = false)
    private LocalDate usageDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
