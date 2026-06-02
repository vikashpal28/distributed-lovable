package com.distributed_lovable_clone.account_service.contoller;


import com.distributed_lovable_clone.account_service.dto.subscription.*;
import com.distributed_lovable_clone.account_service.service.PaymentProcessor;
import com.distributed_lovable_clone.account_service.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;


    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse>  getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest request
            ){
        log.info("checkout session is created with URL {} ");
        return ResponseEntity.ok(paymentProcessor.createCheckoutSession(request));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() throws StripeException, BadRequestException {
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/webhooks/payments")
    public ResponseEntity<String> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ){
        try{
            Event event = Webhook.constructEvent(payload , sigHeader , webhookSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if(deserializer.getObject().isPresent()){
                stripeObject = deserializer.getObject().get();
            }
            else {
                //Failed back from deserialization data
                try{
                 stripeObject = deserializer.deserializeUnsafe();
                 if(stripeObject == null){
                     log.error("Failed to deserialize webhook for event: {}" , event.getType());
                 }
                }
                catch (Exception e){
                    log.error("Unsafe deserialize failed for event {}: {}" , event.getType());
                    return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialize failed");
                }
            }
            //Now extract metaData only if it's a checkout Session
            Map<String , String> metaData = new HashMap<>();
            if(stripeObject instanceof Session session){
                metaData = session.getMetadata();
            }
            //pass to your processor
            paymentProcessor.handleWebhookEvent(event.getType() , stripeObject , metaData);
            return ResponseEntity.ok().build();

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
