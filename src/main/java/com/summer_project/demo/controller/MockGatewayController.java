package com.summer_project.demo.controller;

import com.summer_project.demo.gateway.GatewayVerificationRequest;
import com.summer_project.demo.gateway.GatewayVerificationResult;
import com.summer_project.demo.gateway.PaymentGateway;
import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-gateway")
public class MockGatewayController {
    private final PaymentService paymentService;
    private final PaymentGateway paymentGateway;
    public MockGatewayController(PaymentService paymentService, PaymentGateway paymentGateway) {
        this.paymentService = paymentService;
        this.paymentGateway = paymentGateway;
    }
    @GetMapping(value = "/pay", produces = "text/html")
    public String showPaymentPage(@RequestParam String paymentId, @RequestParam String reference) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Mock Payment Gateway</title>
            </head>
            <body>
                <h1>Mock Payment Gateway</h1>
                <p>Payment ID: %s</p>
                <p>Reference: %s</p>
                <h3>Choose payment result:</h3>
                <form method="POST" action="/mock-gateway/result">
                    <input type="hidden" name="paymentId" value="%s">
                    <input type="hidden" name="reference" value="%s">
                    <input type="hidden" name="outcome" value="SUCCESS">
                    <button type="submit">
                        Pay Successfully
                    </button>
                </form>
                <br>
                <form method="POST" action="/mock-gateway/result">
                    <input type="hidden" name="paymentId" value="%s">
                    <input type="hidden" name="reference" value="%s">
                    <input type="hidden" name="outcome" value="FAILED">
                    <button type="submit">
                        Fail Payment
                    </button>
                </form>
            </body>
            </html>
            """.formatted(paymentId, reference, paymentId, reference, paymentId, reference);
    }
    @PostMapping("/result")
    public String processPaymentResult(@RequestParam String paymentId, @RequestParam String reference, @RequestParam String outcome) {
        // 1. Find payment
        Payment payment = paymentService.getPaymentById(paymentId);
        // 2. Validate gateway reference
        if (!reference.equals(payment.getGatewayReference())) {
            throw new IllegalArgumentException("Invalid gateway reference");
        }
        // 3. Process payment result
        if ("SUCCESS".equals(outcome)) {
            GatewayVerificationResult result = paymentGateway.verifyPayment(new GatewayVerificationRequest(paymentId, reference));
            if (!result.successful()) {
                throw new IllegalArgumentException("Payment verification failed: " + result.message());
            }
            paymentService.updatePaymentStatus(paymentId, PaymentStatus.SUCCESS, result.transactionId());
            return "<h1>Payment Successful!</h1>";
        }
        if ("FAILED".equals(outcome)) {
            String transactionId = "MOCK-FAILED-" + reference.substring(5);
            paymentService.updatePaymentStatus(paymentId, PaymentStatus.FAILED, transactionId);
            return "<h1>Payment Failed!</h1>";
        }
        throw new IllegalArgumentException("Invalid payment outcome");
    }
}