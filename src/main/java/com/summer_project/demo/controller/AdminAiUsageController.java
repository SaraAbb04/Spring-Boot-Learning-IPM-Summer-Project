package com.summer_project.demo.controller;

import com.summer_project.demo.ai.dto.UsageSummaryResponse;
import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.service.AdminAiUsageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/usage")
public class AdminAiUsageController {
    private final AdminAiUsageService adminAiUsageService;
    public AdminAiUsageController(AdminAiUsageService adminAiUsageService) {
        this.adminAiUsageService = adminAiUsageService;
    }
    // 1. Get all usage records with filters
    @GetMapping
    public ResponseEntity<?> getAllUsage(@RequestParam(required = false) String userId, @RequestParam(required = false) String model, @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<AiUsage> usages = adminAiUsageService.getAllUsage(userId, model, status, page, size);
            return ResponseEntity.ok(usages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 2. Get usage records of a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserUsage(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<AiUsage> usages = adminAiUsageService.getUserUsage(userId, page, size);
            return ResponseEntity.ok(usages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 3. Get usage summary of a specific user
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<?> getUserUsageSummary(@PathVariable String userId) {
        try {
            UsageSummaryResponse summary = adminAiUsageService.getUserUsageSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 4. Get one usage record
    @GetMapping("/{usageId}")
    public ResponseEntity<?> getUsageById(@PathVariable String usageId) {
        try {
            AiUsage usage = adminAiUsageService.getUsageById(usageId);
            return ResponseEntity.ok(usage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}