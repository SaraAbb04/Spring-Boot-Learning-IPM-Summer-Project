package com.summer_project.demo.service;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }
    public Payment createPayment(String userId, String planId, BigDecimal amount, String currency) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("Plan ID is required");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPlanId(planId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
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
}
