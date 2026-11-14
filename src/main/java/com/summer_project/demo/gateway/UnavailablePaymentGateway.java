package com.summer_project.demo.gateway;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class UnavailablePaymentGateway implements PaymentGateway {
    @Override
    public GatewayPaymentResponse createPayment(GatewayPaymentRequest request) {
        throw new IllegalStateException("A production payment gateway has not been configured");
    }

    @Override
    public GatewayVerificationResult verifyPayment(GatewayVerificationRequest request) {
        return new GatewayVerificationResult(false, null,
                "A production payment gateway has not been configured");
    }
}
