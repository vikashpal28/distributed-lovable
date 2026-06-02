package com.distributed_lovable_clone.account_service.service.impl;


import com.distributed_lovable_clone.account_service.dto.subscription.CheckoutRequest;
import com.distributed_lovable_clone.account_service.dto.subscription.CheckoutResponse;
import com.distributed_lovable_clone.account_service.dto.subscription.PortalResponse;
import com.distributed_lovable_clone.account_service.entity.User;
import com.distributed_lovable_clone.account_service.repository.PlanRepository;
import com.distributed_lovable_clone.account_service.repository.UserRepository;
import com.distributed_lovable_clone.account_service.service.PaymentProcessor;
import com.distributed_lovable_clone.account_service.service.SubscriptionService;
import com.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.common_lib.type.SubscriptionStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Value("${app.frontend.url}")
    private String frontend;
    @Override
    public CheckoutResponse createCheckoutSession(CheckoutRequest request) {
        com.distributed_lovable_clone.account_service.entity.Plan plan = planRepository.findById(request.planId()).orElseThrow(()->
                new ResourceNotFoundException("Plan is not found" , request.planId().toString()));
        Long userId  = authUtil.getCurrentUserId();
        User user = getUser(userId);
      log.info("Checkout session {}" , request.planId());
        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(
                                plan.getStripePriceId()

                        ).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE).build())
                                .build()
                )
                .setSuccessUrl(frontend + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontend + "/cancel.html")
                .putMetadata("user_id" , userId.toString())
                .putMetadata("plan_id" , plan.getId().toString());
        try {
            String stripeCustomerId = user.getStripeCustomerId();

            if(stripeCustomerId == null || stripeCustomerId.isEmpty()){
                params.setCustomerEmail(user.getUsername());
            }
            else{
                params.setCustomer(stripeCustomerId);
            }


            log.info("attempting to get the checkout session....");
            try {
                Session session = Session.create(params.build());
                return new CheckoutResponse(session.getUrl());
            }
            catch (StripeException e){
             log.error("Stripe checkout session failed..." , e.getMessage() , e);
             throw new RuntimeException(e);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }



    @Override
    public PortalResponse openCustomerPortal() throws BadRequestException, StripeException {
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);
        String stripeCustomerId = user.getStripeCustomerId();

        if(stripeCustomerId == null || stripeCustomerId.isEmpty()){
            throw new BadRequestException("userId does not have a stripe Customer Id, UserId:"+userId);
        }

        var portalSession = com.stripe.model.billingportal.Session.create(
                com.stripe.param.billingportal.SessionCreateParams.builder()
                        .setCustomer(stripeCustomerId)
                        .setReturnUrl(frontend)
                        .build()
        );
        return new PortalResponse(portalSession.getUrl());
    }

    @Override
    @Async
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData) {
    log.info("type  {}", type);
    switch (type){
        case  "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metaData); // one-time , on checkout completed
        case  "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject); // when userId cancels , upgrades or any other updates
        case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject); // when subscription end then revoke
        case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject); // when invoice paid
        case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject); // when invoice is not paid, mark as PAST_DUE
        default -> log.debug("Ignore the event {}" , type);

    }
    }

    private void handleCheckoutSessionCompleted(Session session , Map<String , String> metadata){
//        at createSession we passed the metadata that's we passed the metaData
        if(session == null){
            log.error("session object was null");
            return;
        }


            Long userId = Long.valueOf(metadata.get("user_id"));
            Long planId = Long.valueOf(metadata.get("plan_id"));

            String subscriptionId = session.getSubscription();
            String customerId = session.getCustomer();
            log.info("Stripe customer ID: {}", customerId);

            User user = getUser(userId);
            if (user.getStripeCustomerId() == null) {
                user.setStripeCustomerId(customerId);
                userRepository.save(user);
            }

        try {
            // ... your existing logic ...
            log.info("Activating subscription for User: {} Plan: {}", userId, planId);
            subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);
            log.info("Subscription successfully activated in DB");
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Conflict detected: Subscription already being processed.");
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: Check for nulls or FK issues: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save subscription to database", e);
            throw e; // Rethrow to let Stripe know the webhook failed (will retry)
        }

    }

    private void handleCustomerSubscriptionUpdated(Subscription subscription){
        if(subscription == null){
            log.error("subscription object was null");
            return;
        }
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if (status == null){
            log.warn("unknown status '{}' for subscription {}" , subscription.getId());
            return;
        }

        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(subscription.getCreated());
        Instant periodEnd = toInstant(subscription.getEndedAt());

        Long planId = resolvePlanId(item.getPrice());
        subscriptionService.updateSubscription(
                subscription.getId() , status , periodStart , periodEnd ,subscription.getCancelAtPeriodEnd() , planId
        );
        
    }



    private void handleCustomerSubscriptionDeleted(Subscription subscription){
        if (subscription == null) {
            log.error("Subscription object was null in handleCustomerSubscriptionDeleted");
            return;
        }

        String stripeSubId = subscription.getId();
        log.info("Received deletion webhook for Stripe Subscription: {}", stripeSubId);

        try {
            // Pass the stripe ID to your service to locate the local record
            subscriptionService.cancelSubscription(stripeSubId);
            log.info("Successfully processed cancellation for: {}", stripeSubId);
        } catch (ResourceNotFoundException e) {
            // If the sub isn't in our DB, it might have been a test sub or deleted manually
            log.warn("Attempted to cancel non-existent subscription in DB: {}", stripeSubId);
        } catch (Exception e) {
            log.error("Failed to process subscription deletion for {}. Error: {}", stripeSubId, e.getMessage());
            // Re-throw the original exception (or wrap it) so Stripe knows to retry
            throw new RuntimeException("Error processing Stripe cancellation", e);
        }
    }

    private void handleInvoicePaid(Invoice invoice)  {
        String subId = extractSubscriptionId(invoice);
        if(subId == null) return;
        try {
            Subscription subscription = Subscription.retrieve(subId); //sdk calling the stripe server
            var item = subscription.getItems().getData().get(0);

            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(subId, periodStart, periodEnd);
        } catch (StripeException e){
            throw new RuntimeException(e);
        }
    }

    private void handleInvoicePaymentFailed(Invoice invoice){
        String subId = extractSubscriptionId(invoice);
        if(subId == null) return;
        subscriptionService.markSubscriptionPastDue(subId);
    }

    private @NonNull User getUser(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("userId is already exists" , userId.toString()));
        return user;
    }

    private Long resolvePlanId(Price price) {
        if(price == null || price.getId() == null) return null;
        return planRepository.findByStripePriceId(price.getId())
                .map(com.distributed_lovable_clone.account_service.entity.Plan::getId)
                .orElse(null);
    }

    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    private SubscriptionStatus mapStripeStatusToEnum( String status) {
        return switch (status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRAILING;
            case "past_due" ,"unpaid" , "paused" , "Incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("unmapped Stripe status: {}" , status);
                yield null;
            }
        };
    }

    private String extractSubscriptionId(Invoice invoice){
        var parent = invoice.getParent();
        if(parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if(subDetails == null) return  null;

        return subDetails.getSubscription();
    }

}
