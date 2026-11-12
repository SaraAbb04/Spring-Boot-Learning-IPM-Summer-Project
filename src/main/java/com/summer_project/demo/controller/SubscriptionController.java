package com.summer_project.demo.controller;

import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import com.summer_project.demo.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }
    @PostMapping
    public ResponseEntity<?> createSubscription(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String planId = request.get("planId");
            Subscription subscription = subscriptionService.createSubscription(userId, planId);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<List<Subscription>>
    getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>>
    getSubscriptionsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }
    @PostMapping("/free")
    public ResponseEntity<?> activateFreePlan(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
            String planId = request.get("planId");
            Subscription subscription = subscriptionService.activateFreePlan(user.getId(), planId);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
