package com.vsignai.backend.entity;

import com.vsignai.backend.enums.feature.LimitUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "plan_features",
        uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "feature_id"}),
        indexes = {@Index(name = "idx_plan_feature", columnList = "plan_id, feature_id"),
                   @Index(name = "idx_feature", columnList = "feature_id")
        }

)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    private Integer limitValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitUnit unit; // SECOND, REQUEST

    @Column(nullable = false)
    private Boolean isEnabled = false;
}
