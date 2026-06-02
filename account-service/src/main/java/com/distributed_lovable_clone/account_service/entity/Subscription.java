package com.distributed_lovable_clone.account_service.entity;


import com.distributed_lovable_clone.common_lib.type.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false , name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false , name = "plan_id")
    Plan plan;


    @Enumerated(value = EnumType.STRING)
    SubscriptionStatus status;

    String stripeSubscriptionId; // can be name as gatewaySubscriptionId

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    @Builder.Default
    @Column(name = "cancel_at_period_end", nullable = false)
    Boolean cancelAtPeriodEnd = false; // Changed from Boolean to boolean
    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

}
