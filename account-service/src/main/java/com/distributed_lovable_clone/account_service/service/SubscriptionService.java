package com.distributed_lovable_clone.account_service.service;


import com.distributed_lovable_clone.account_service.dto.subscription.SubscriptionResponse;
import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.type.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {

    SubscriptionResponse getCurrentSubscription();


    void activateSubscription(Long userId, Long planId, String gateSubscriptionId, String customerId);

    void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String gatewaySubscriptionId);

    PlanDto getCurrentSubscribedPlanByUser();

//    boolean canCreateNewProject();
}
