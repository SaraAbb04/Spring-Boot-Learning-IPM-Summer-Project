package com.summer_project.demo.gateway;

public interface PaymentGateway {
    GatewayPaymentResponse createPayment(GatewayPaymentRequest request);
    GatewayVerificationResult verifyPayment(GatewayVerificationRequest request);
}
