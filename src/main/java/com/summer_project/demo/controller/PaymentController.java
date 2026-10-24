package com.summer_project.demo.controller;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import com.summer_project.demo.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }
    @PostMapping
    public ResponseEntity<?> createPayment(Authentication authentication, @RequestBody CreatePaymentRequest request) {
        try {
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
            Payment payment = paymentService.createPayment(user.getId(), request.planId());
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyPayments(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Payment> payments = paymentService.getPaymentsByUserId(user.getId());
        return ResponseEntity.ok(payments);
    }
    public record CreatePaymentRequest(String planId) {}
}
