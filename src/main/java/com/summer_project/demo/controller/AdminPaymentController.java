package com.summer_project.demo.controller;

import com.summer_project.demo.model.Payment;
import com.summer_project.demo.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    private final PaymentService paymentService;
    public AdminPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @GetMapping("/processing")
    public List<Payment> getProcessingPayments() {
        return paymentService.getProcessingPayments();
    }
}