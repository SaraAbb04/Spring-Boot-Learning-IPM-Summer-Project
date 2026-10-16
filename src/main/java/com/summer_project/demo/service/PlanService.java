package com.summer_project.demo.service;

import com.summer_project.demo.dto.PlanRequest;
import com.summer_project.demo.model.Plan;
import com.summer_project.demo.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    public PlanService(PlanRepository planRepository){
        this.planRepository = planRepository;
    }
    public Plan createPlan(PlanRequest request) {
        validatePlan(request);
        String name = request.name().trim();
        if (planRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("A plan with this name already exists");
        }
        Plan plan = new Plan(name, request.description() == null ? "" : request.description().trim(), request.price(), request.currency().trim().toUpperCase(), request.billingPeriod().trim().toUpperCase(), request.maxRequests(), request.maxTokens(), request.active(), LocalDateTime.now());
        return planRepository.save(plan);
    }
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }
    public List<Plan> getActivePlans() {
        return planRepository.findByActiveTrue();
    }
    public Optional<Plan> getPlanById(String id) {
        return planRepository.findById(id);
    }
    private void validatePlan(PlanRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Plan request is required");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (request.price() == null || request.price().signum() < 0) {
            throw new IllegalArgumentException("Plan price must be non-negative");
        }
        if (request.currency() == null || request.currency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (request.billingPeriod() == null || request.billingPeriod().isBlank()) {
            throw new IllegalArgumentException("Billing period is required");
        }
        String period = request.billingPeriod().trim().toUpperCase();
        if (!List.of("MONTHLY", "YEARLY", "ONE_TIME").contains(period)) {
            throw new IllegalArgumentException("Billing period must be MONTHLY, YEARLY, or ONE_TIME");
        }
        if (request.maxRequests() < 0 || request.maxTokens() < 0) {
            throw new IllegalArgumentException("Usage limits must be non-negative");
        }
    }
}
