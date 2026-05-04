package com.vsignai.backend.entity;


import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;

    private LocalDateTime canceledAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
