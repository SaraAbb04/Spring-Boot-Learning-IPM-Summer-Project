package com.summer_project.demo.controller;

import com.summer_project.demo.dto.PlanRequest;
import com.summer_project.demo.model.Plan;
import com.summer_project.demo.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/plans")
public class PlanController {
    private final PlanService planService;
    public PlanController(PlanService planService){
        this.planService = planService;
    }
    @PostMapping
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest request) {
        try {
            Plan plan = planService.createPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
    @GetMapping("/active")
    public ResponseEntity<List<Plan>> getActivePlans() {
        return ResponseEntity.ok(planService.getActivePlans());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable String id) {
        return planService.getPlanById(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Plan not found")));
    }
}
