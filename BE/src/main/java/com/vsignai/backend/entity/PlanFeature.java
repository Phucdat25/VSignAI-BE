package com.vsignai.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plan_features",
        uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "feature_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;

    private Integer limitValue;
}
