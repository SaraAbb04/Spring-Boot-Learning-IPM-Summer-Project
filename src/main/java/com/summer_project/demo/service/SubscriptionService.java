package com.summer_project.demo.service;

import com.summer_project.demo.model.Plan;
import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.model.SubscriptionStatus;
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
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active");
        }
        List<Subscription> activeSubscriptions = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE.name());
        LocalDateTime now = LocalDateTime.now();
        for (Subscription existingSubscription : activeSubscriptions) {
            if (existingSubscription.getEndDate() != null && !existingSubscription.getEndDate().isAfter(now)) {
                existingSubscription.setStatus(SubscriptionStatus.EXPIRED.name());
                subscriptionRepository.save(existingSubscription);
            }
        }
        if (subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE.name())) {
            throw new IllegalArgumentException("User already has an active subscription");
        }
        LocalDateTime endDate;
        switch (plan.getBillingPeriod().toUpperCase()) {
            case "MONTHLY" -> endDate = now.plusMonths(1);
            case "YEARLY" -> endDate = now.plusYears(1);
            case "ONE_TIME" -> endDate = null;
            default -> throw new IllegalArgumentException("Unsupported billing period");
        }
        Subscription subscription = new Subscription(userId, planId, SubscriptionStatus.ACTIVE.name(), now, endDate, now);
        return subscriptionRepository.save(subscription);
    }
    public List<Subscription> getSubscriptionsByUserId(String userId) {
        return subscriptionRepository.findByUserId(userId);
    }
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}