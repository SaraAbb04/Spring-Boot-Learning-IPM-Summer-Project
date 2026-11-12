package com.summer_project.demo.controller;

import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.service.SubscriptionService;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
public class UserSubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    public UserSubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }
    @PostMapping("/free")
    public ResponseEntity<?> activateFreePlan(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            // 1. Get current user's email from JWT authentication
            String email = authentication.getName();
            // 2. Find user by email
            var user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
            // 3. Get planId from request
            String planId = request.get("planId");
            // 4. Activate free plan
            Subscription subscription = subscriptionService.activateFreePlan(user.getId(), planId);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}