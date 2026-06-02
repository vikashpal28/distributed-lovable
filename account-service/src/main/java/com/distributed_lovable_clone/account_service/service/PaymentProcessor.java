package com.distributed_lovable_clone.account_service.service;


import com.distributed_lovable_clone.account_service.dto.subscription.CheckoutRequest;
import com.distributed_lovable_clone.account_service.dto.subscription.CheckoutResponse;
import com.distributed_lovable_clone.account_service.dto.subscription.PortalResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import org.apache.coyote.BadRequestException;

import java.util.Map;

public interface PaymentProcessor {

     CheckoutResponse createCheckoutSession(CheckoutRequest request);

     PortalResponse openCustomerPortal() throws BadRequestException, StripeException;

     void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
