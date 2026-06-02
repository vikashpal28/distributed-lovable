package com.distributed_lovable_clone.account_service.service.impl;


import com.distributed_lovable_clone.account_service.dto.subscription.SubscriptionResponse;
import com.distributed_lovable_clone.account_service.entity.Plan;
import com.distributed_lovable_clone.account_service.entity.Subscription;
import com.distributed_lovable_clone.account_service.entity.User;
import com.distributed_lovable_clone.account_service.mapper.SubscriptionMapper;
import com.distributed_lovable_clone.account_service.repository.PlanRepository;
import com.distributed_lovable_clone.account_service.repository.SubscriptionRepository;
import com.distributed_lovable_clone.account_service.repository.UserRepository;
import com.distributed_lovable_clone.account_service.service.SubscriptionService;
import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.common_lib.type.SubscriptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;


    @Override

    public SubscriptionResponse getCurrentSubscription() {

        try {

            Long userId = authUtil.getCurrentUserId();

            var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId , Set.of(

                    SubscriptionStatus.ACTIVE , SubscriptionStatus.PAST_DUE,

                    SubscriptionStatus.TRAILING

            )).orElse(

                    new Subscription()

            );

            return subscriptionMapper.toSubscriptionResponse(currentSubscription);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }



    }

    @Override
    public void activateSubscription(Long userId, Long planId, String gateSubscriptionId, String customerId) {
    boolean exists = subscriptionRepository.existsByStripeSubscriptionId(gateSubscriptionId);
    if(exists) return;
    User user = getUser(userId);
    Plan plan = getPlan(planId);


        try {
        log.info("activation section");
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(gateSubscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .createdAt(Instant.now())
                .build();
        subscriptionRepository.save(subscription);
    }
    catch (Exception e) {
        log.error("It is stored ", e.getMessage() ,  user , plan);
        throw new RuntimeException(e.getMessage());
    }
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Boolean hasSubscriptionUpdated = false;
    Subscription subscription = getSubscription(gatewaySubscriptionId);
    if(status != null && status != subscription.getStatus()){
        subscription.setStatus(status);
        hasSubscriptionUpdated = true;
    }

    if(periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())){
        subscription.setCurrentPeriodStart(periodStart);
        hasSubscriptionUpdated = true;
    }

    if(periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())){
        subscription.setCurrentPeriodEnd(periodEnd);
        hasSubscriptionUpdated = true;
    }

    if(cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()){
        subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
    }

    if(planId != null && !planId.equals(subscription.getPlan().getId())){
        Plan newPlan = getPlan(planId);
        subscription.setPlan(newPlan);
        hasSubscriptionUpdated = true;
    }
     if( hasSubscriptionUpdated) {
         log.debug("subscription services has been updated {}" , gatewaySubscriptionId);
         subscriptionRepository.save(subscription);
     }
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseGet(() -> {
                    log.info("Invoice arrived before creation transaction completed. Resolving identity metrics for lazy shell: {}", gatewaySubscriptionId);

                    try {
                        // 1. Query the absolute state of the subscription directly from the Stripe API
                        com.stripe.model.Subscription stripeSub = com.stripe.model.Subscription.retrieve(gatewaySubscriptionId);
                        String stripeCustomerId = stripeSub.getCustomer();
                        String stripePriceId = stripeSub.getItems().getData().get(0).getPrice().getId();

                        // 2. Locate your local User via their saved Stripe Customer ID
                        User user = userRepository.findByStripeCustomerId(stripeCustomerId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found for Stripe Customer ID: " , stripeCustomerId));

                        // 3. Locate your local Plan via the mapped Stripe Price ID
                        Plan plan = planRepository.findByStripePriceId(stripePriceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Plan not mapped for Stripe Price ID: " , stripePriceId));

                        // 4. Return a fully valid, structurally sound record shell that satisfies PostgreSQL constraints
                        return Subscription.builder()
                                .stripeSubscriptionId(gatewaySubscriptionId)
                                .user(user)
                                .plan(plan)
                                .status(SubscriptionStatus.INCOMPLETE)
                                .createdAt(Instant.now())
                                .build();

                    } catch (Exception e) {
                        log.error("Failed to safely resolve lazy subscription shell metrics for ID: {}", gatewaySubscriptionId, e);
                        throw new RuntimeException("Cannot resolve database constraints for lazy subscription fallback", e);
                    }
                });

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd != null ? periodEnd : newStart);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
        log.info("Subscription period successfully renewed and synchronized for ID: {}", gatewaySubscriptionId);
    }
    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE){
            log.debug("Subscription is already past due, gatewaySubscription {}" , gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        //Notify userId via email..
    }

    @Override
    public PlanDto getCurrentSubscribedPlanByUser() {
        SubscriptionResponse subscriptionResponse = getCurrentSubscription();
        return subscriptionResponse.plan();
    }

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("userId" , userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId).orElseThrow(() ->
                new ResourceNotFoundException("plan" , planId.toString())
        );
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId).orElseThrow(()->
                new ResourceNotFoundException("subscription " , gatewaySubscriptionId));
    }

}
