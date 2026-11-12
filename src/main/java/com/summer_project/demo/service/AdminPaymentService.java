package com.summer_project.demo.service;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminPaymentService {
    private final PaymentRepository paymentRepository;
    private final MongoTemplate mongoTemplate;
    public AdminPaymentService(PaymentRepository paymentRepository, MongoTemplate mongoTemplate) {
        this.paymentRepository = paymentRepository;
        this.mongoTemplate = mongoTemplate;
    }
    // 1. Get all payments with optional filters
    public Page<Payment> getAllPayments(String userId, String planId, String status, String transactionId, int page, int size) {
        validatePagination(page, size);
        List<Criteria> criteriaList = new ArrayList<>();
        if (userId != null && !userId.isBlank()) {
            criteriaList.add(Criteria.where("userId").is(userId));
        }
        if (planId != null && !planId.isBlank()) {
            criteriaList.add(Criteria.where("planId").is(planId));
        }
        if (status != null && !status.isBlank()) {
            PaymentStatus paymentStatus = validateStatus(status);
            criteriaList.add(Criteria.where("status").is(paymentStatus.name()));
        }
        if (transactionId != null && !transactionId.isBlank()) {
            criteriaList.add(Criteria.where("transactionId").is(transactionId));
        }
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        // Count before applying pagination
        long total = mongoTemplate.count(query, Payment.class);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageable);
        List<Payment> payments = mongoTemplate.find(query, Payment.class);
        return new PageImpl<>(payments, pageable, total);
    }
    // 2. Get payments of a specific user
    public Page<Payment> getPaymentsByUserId(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return getAllPayments(userId, null, null, null, page, size);
    }
    // 3. Get payment by ID
    public Payment getPaymentById(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        return paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
    // 4. Get payments by status
    public List<Payment> getPaymentsByStatus(String status) {
        PaymentStatus paymentStatus = validateStatus(status);
        return paymentRepository.findByStatus(paymentStatus.name());
    }
    // 5. Validate payment status
    private PaymentStatus validateStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment status. Allowed values: PENDING, PROCESSING, SUCCESS, FAILED");
        }
    }
    // 6. Validate pagination
    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }
}