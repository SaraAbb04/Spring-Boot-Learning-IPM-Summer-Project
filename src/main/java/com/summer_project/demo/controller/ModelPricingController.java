package com.summer_project.demo.controller;

import org.springframework.dao.DuplicateKeyException;
import com.summer_project.demo.dto.ModelPricingRequest;
import com.summer_project.demo.model.ModelPricing;
import com.summer_project.demo.service.ModelPricingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/model-pricing")
public class ModelPricingController {
    private final ModelPricingService modelPricingService;
    public ModelPricingController(ModelPricingService modelPricingService) {
        this.modelPricingService = modelPricingService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModelPricing createPricing(@RequestBody ModelPricingRequest request) {
        return modelPricingService.createPricing(request);
    }
    @GetMapping
    public List<ModelPricing> getAllPricing() {
        return modelPricingService.getAllPricing();
    }
    @PutMapping("/{id}")
    public ModelPricing updatePricing(@PathVariable String id, @RequestBody ModelPricingRequest request) {
        return modelPricingService.updatePricing(id, request);
    }
    //Duplicate Testing in ExceptionHandler
    @GetMapping("/test-conflict")
    public void testConflict() {
        throw new DuplicateKeyException("Test duplicate key exception");
    }
}
