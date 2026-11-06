package com.summer_project.demo.service;

import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.model.Plan;
import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.model.SubscriptionStatus;
import com.summer_project.demo.repository.AiUsageRepository;
import com.summer_project.demo.repository.PlanRepository;
import com.summer_project.demo.repository.SubscriptionRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final AiUsageRepository aiUsageRepository;
    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository, UserRepository userRepository, AiUsageRepository aiUsageRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.aiUsageRepository = aiUsageRepository;
    }
    public Subscription createSubscription(String userId, String planId) {
        return createSubscriptionInternal(userId, planId, null);
    }
    public Subscription createSubscriptionForPayment(String userId, String planId, String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("Plan ID is required");
        }
        var existingSubscription = subscriptionRepository.findByPaymentId(paymentId);
        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            if (!subscription.getUserId().equals(userId) || !subscription.getPlanId().equals(planId)) {
                throw new IllegalArgumentException("Payment is already linked to another subscription");
            }
            return subscription;
        }
        return createSubscriptionInternal(userId, planId, paymentId);
    }
    private Subscription createSubscriptionInternal(String userId, String planId, String paymentId) {
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
        subscription.setPaymentId(paymentId);
        try {
            return subscriptionRepository.save(subscription);

        } catch (DuplicateKeyException duplicateKeyException) {
            // If this subscription belongs to a payment,
            // check whether another request has already created it.
            if (paymentId != null) {
                Subscription existingSubscription = subscriptionRepository.findByPaymentId(paymentId).orElse(null);
                if (existingSubscription != null && Objects.equals(existingSubscription.getUserId(), userId) && Objects.equals(existingSubscription.getPlanId(), planId)) {
                    return existingSubscription;
                }
            }
            throw duplicateKeyException;
        }
    }
    public void checkUsageLimits(String userId) {
        LocalDateTime now = LocalDateTime.now();
        // 1. Find the user's active subscription
        Subscription subscription = subscriptionRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, SubscriptionStatus.ACTIVE.name()).orElseThrow(() -> new IllegalArgumentException("User does not have an active subscription"));
        // 2. Check subscription expiration
        if (subscription.getEndDate() != null && !subscription.getEndDate().isAfter(now)) {
            subscription.setStatus(SubscriptionStatus.EXPIRED.name());
            subscriptionRepository.save(subscription);
            throw new IllegalArgumentException("Subscription has expired");
        }
        // 3. Find the plan
        Plan plan = planRepository.findById(subscription.getPlanId()).orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        // 4. Get successful usage during this subscription period
        List<AiUsage> usages = aiUsageRepository.findUsageInPeriod(userId, "SUCCESS", subscription.getStartDate(), now);
        // 5. Calculate consumption
        long totalRequests = usages.size();
        long totalTokens = usages.stream().mapToLong(AiUsage::getTotalTokens).sum();
        // 6. Check request limit
        if (totalRequests >= plan.getMaxRequests()) {
            throw new IllegalArgumentException("Request limit exceeded");
        }
        // 7. Check token limit
        if (totalTokens >= plan.getMaxTokens()) {
            throw new IllegalArgumentException("Token limit exceeded");
        }
    }
    public List<Subscription> getSubscriptionsByUserId(String userId) {
        return subscriptionRepository.findByUserId(userId);
    }
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}