package com.summer_project.demo.repository;

import com.summer_project.demo.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    List<Payment> findByUserIdAndStatus(String userId, String status);
    Optional<Payment> findByTransactionId(String transactionId);
}
