package com.summer_project.demo.gateway;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Profile("dev")
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public GatewayPaymentResponse createPayment(GatewayPaymentRequest request) {
        String reference = "MOCK-" + UUID.randomUUID();
        String redirectUrl = "http://localhost:8080/mock-gateway/pay?paymentId=" + URLEncoder.encode(request.paymentId(), StandardCharsets.UTF_8) + "&reference=" + URLEncoder.encode(reference, StandardCharsets.UTF_8);
        return new GatewayPaymentResponse(redirectUrl, reference);
    }
    @Override
    public GatewayVerificationResult verifyPayment(GatewayVerificationRequest request) {
        if (request.paymentId() == null || request.paymentId().isBlank()) {
            return new GatewayVerificationResult(false, null, "Payment ID is required");
        }
        if (request.gatewayReference() == null || !request.gatewayReference().startsWith("MOCK-")) {
            return new GatewayVerificationResult(false, null, "Invalid mock gateway reference");
        }
        String transactionId = "MOCK-TXN-" + request.gatewayReference().substring(5);
        return new GatewayVerificationResult(true, transactionId, "Mock payment verified successfully");
    }
}