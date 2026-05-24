package com.vsignai.backend.entity;


import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_subscriptions",
        indexes = {
                @Index(name = "idx_subscription_user", columnList = "user_id"),
                @Index(name = "idx_subscription_status", columnList = "status"),
                @Index(name = "idx_subscription_end", columnList = "current_period_end")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAutoRenew = false;

    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime currentPeriodStart;

    @Column(nullable = false)
    private LocalDateTime currentPeriodEnd;

    private LocalDateTime canceledAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean cancelAtPeriodEnd = false;
}
