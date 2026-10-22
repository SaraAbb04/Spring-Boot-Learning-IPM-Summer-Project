package com.summer_project.demo.repository;

import com.summer_project.demo.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByUserId(String userId);
    Optional<Subscription> findFirstByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
    List<Subscription> findByStatus(String status);
    boolean existsByUserIdAndStatus(String userId, String status);
    List<Subscription> findByUserIdAndStatus(String userId, String status);
}
