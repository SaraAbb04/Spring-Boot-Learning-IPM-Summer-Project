package com.summer_project.demo.service;

import com.summer_project.demo.model.Plan;
import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.repository.PlanRepository;
import com.summer_project.demo.repository.SubscriptionRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }
    public Subscription createSubscription(String userId, String planId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("Plan ID is required");
        }
        if (subscriptionRepository.existsByUserIdAndStatus(userId, "ACTIVE")) {
            throw new IllegalArgumentException("User already has an active subscription");
        }
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate;
        switch (plan.getBillingPeriod().toUpperCase()) {
            case "MONTHLY" -> endDate = now.plusMonths(1);
            case "YEARLY" -> endDate = now.plusYears(1);
            case "ONE_TIME" -> endDate = null;
            default -> throw new IllegalArgumentException("Unsupported billing period");
        }
        Subscription subscription = new Subscription(userId, planId, "ACTIVE", now, endDate, now);
        return subscriptionRepository.save(subscription);
    }
    public List<Subscription> getSubscriptionsByUserId(String userId) {
        return subscriptionRepository.findByUserId(userId);
    }
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}
