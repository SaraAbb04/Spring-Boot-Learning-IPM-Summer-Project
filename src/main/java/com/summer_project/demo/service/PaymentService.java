package com.summer_project.demo.service;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.model.Plan;
import com.summer_project.demo.repository.PaymentRepository;
import com.summer_project.demo.repository.PlanRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PlanRepository planRepository;
    private final SubscriptionService subscriptionService;
    public PaymentService(PaymentRepository paymentRepository, PlanRepository planRepository, SubscriptionService subscriptionService){
        this.paymentRepository = paymentRepository;
        this.planRepository = planRepository;
        this.subscriptionService = subscriptionService;
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
        return paymentRepository.save(payment);
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
        if (newStatus == null || newStatus == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Invalid payment status");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (!PaymentStatus.PENDING.name().equals(payment.getStatus())) {
            throw new IllegalArgumentException("Payment has already been processed");
        }
        var existingPayment = paymentRepository.findByTransactionId(transactionId);
        if (existingPayment.isPresent() && !existingPayment.get().getId().equals(paymentId)) {
            throw new IllegalArgumentException("Transaction ID already exists");
        }
        // If payment is successful, create the subscription first.
        if (newStatus == PaymentStatus.SUCCESS) {
            subscriptionService.createSubscription(payment.getUserId(), payment.getPlanId());
        }
        payment.setStatus(newStatus.name());
        payment.setTransactionId(transactionId);
        payment.setUpdatedAt(LocalDateTime.now());
        try{
            return paymentRepository.save(payment);
        }
        catch(DuplicateKeyException exception){
            throw new IllegalArgumentException("Transaction ID already exists");
        }
    }
}
