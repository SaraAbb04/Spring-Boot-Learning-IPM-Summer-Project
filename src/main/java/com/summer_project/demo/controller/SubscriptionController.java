package com.summer_project.demo.controller;

import com.summer_project.demo.model.Subscription;
import com.summer_project.demo.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
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
}
