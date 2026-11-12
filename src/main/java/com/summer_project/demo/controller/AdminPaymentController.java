package com.summer_project.demo.controller;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.service.AdminPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;
    public AdminPaymentController(AdminPaymentService adminPaymentService) {
        this.adminPaymentService = adminPaymentService;
    }
    // 1. Get all payments with filters
    @GetMapping
    public ResponseEntity<?> getAllPayments(@RequestParam(required = false) String userId, @RequestParam(required = false) String planId, @RequestParam(required = false) String status, @RequestParam(required = false) String transactionId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Payment> payments = adminPaymentService.getAllPayments(userId, planId, status, transactionId, page, size);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 2. Get payments of a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Payment> payments = adminPaymentService.getPaymentsByUserId(userId, page, size);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 3. Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable String status) {
        try {
            List<Payment> payments = adminPaymentService.getPaymentsByStatus(status);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 4. Get processing payments
    @GetMapping("/processing")
    public ResponseEntity<List<Payment>> getProcessingPayments() {
        List<Payment> payments = adminPaymentService.getPaymentsByStatus("PROCESSING");
        return ResponseEntity.ok(payments);
    }
    // 5. Get payment by ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId) {
        try {
            Payment payment = adminPaymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}