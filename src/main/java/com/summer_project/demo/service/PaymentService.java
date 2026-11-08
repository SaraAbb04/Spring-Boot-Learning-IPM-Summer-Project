package com.summer_project.demo.service;

import com.summer_project.demo.gateway.GatewayPaymentRequest;
import com.summer_project.demo.gateway.GatewayPaymentResponse;
import com.summer_project.demo.gateway.PaymentGateway;
import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.model.Plan;
import com.summer_project.demo.repository.PaymentRepository;
import com.summer_project.demo.repository.PlanRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PlanRepository planRepository;
    private final SubscriptionService subscriptionService;
    private final PaymentGateway paymentGateway;
    @Value("${payment.callback-url}")
    private String callbackUrl;
    public PaymentService(PaymentRepository paymentRepository, PlanRepository planRepository, SubscriptionService subscriptionService, PaymentGateway paymentGateway){
        this.paymentRepository = paymentRepository;
        this.planRepository = planRepository;
        this.subscriptionService = subscriptionService;
        this.paymentGateway = paymentGateway;
    }
    public Payment createPayment(String userId, String planId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("Plan ID is required");
        }
        Plan plan = planRepository.findById(planId).orElseThrow(()-> new IllegalArgumentException("Plan not found"));
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active");
        }
        if (plan.getPrice() == null || plan.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Invalid plan price");
        }
        if (plan.getCurrency() == null || plan.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Invalid plan currency");
        }
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPlanId(plan.getId());
        payment.setAmount(plan.getPrice());
        payment.setCurrency(plan.getCurrency());
        payment.setStatus(PaymentStatus.PENDING.name());
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        // Step 1: Save payment in database
        Payment savedPayment = paymentRepository.save(payment);
        // Step 2: Create gateway request
        GatewayPaymentRequest gatewayRequest = new GatewayPaymentRequest(savedPayment.getId(), savedPayment.getAmount(), savedPayment.getCurrency(), callbackUrl, "Payment for plan: " + plan.getName());
        // Step 3: Send request to gateway
        GatewayPaymentResponse gatewayResponse = paymentGateway.createPayment(gatewayRequest);
        // Step 4: Save gateway response
        savedPayment.setGatewayReference(gatewayResponse.gatewayReference());
        savedPayment.setRedirectUrl(gatewayResponse.redirectUrl());
        savedPayment.setUpdatedAt(LocalDateTime.now());
        // Step 5: Return updated payment
        return paymentRepository.save(savedPayment);
    }
    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
    public Payment updatePaymentStatus(String paymentId, PaymentStatus newStatus, String transactionId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        if (newStatus == null || newStatus == PaymentStatus.PENDING || newStatus == PaymentStatus.PROCESSING) {
            throw new IllegalArgumentException("Invalid payment status");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        // Handle repeated successful callbacks
        if (PaymentStatus.SUCCESS.name().equals(payment.getStatus())) {
            if (transactionId.equals(payment.getTransactionId())) {
                return payment;
            }
            throw new IllegalArgumentException("Payment has already been processed");
        }
        // A failed payment cannot be processed again
        if (PaymentStatus.FAILED.name().equals(payment.getStatus())) {
            throw new IllegalArgumentException("Payment has already been processed");
        }
        // Prevent changing an existing transaction ID
        if (payment.getTransactionId() != null && !payment.getTransactionId().equals(transactionId)) {
            throw new IllegalArgumentException("Transaction ID does not match");
        }
        // Check transaction ID uniqueness
        var existingPayment = paymentRepository.findByTransactionId(transactionId);
        if (existingPayment.isPresent() && !existingPayment.get().getId().equals(paymentId)) {
            throw new IllegalArgumentException("Transaction ID already exists");
        }
        // Handle failed payment
        if (newStatus == PaymentStatus.FAILED) {
            if (!PaymentStatus.PENDING.name().equals(payment.getStatus())) {
                throw new IllegalArgumentException("Payment cannot be marked as failed");
            }
            payment.setStatus(PaymentStatus.FAILED.name());
            payment.setTransactionId(transactionId);
            payment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        }
        // Step 1: Save PROCESSING status
        payment.setStatus(PaymentStatus.PROCESSING.name());
        payment.setTransactionId(transactionId);
        payment.setUpdatedAt(LocalDateTime.now());
        try {
            paymentRepository.save(payment);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Transaction ID already exists");
        }
        // Step 2: Create or retrieve subscription
        subscriptionService.createSubscriptionForPayment(payment.getUserId(), payment.getPlanId(), payment.getId());
        // Step 3: Mark payment as SUCCESS
        payment.setStatus(PaymentStatus.SUCCESS.name());
        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
    public List<Payment> getProcessingPayments() {
        return paymentRepository.findByStatus(
                PaymentStatus.PROCESSING.name()
        );
    }
}
