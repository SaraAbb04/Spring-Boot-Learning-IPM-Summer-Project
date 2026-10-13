package com.summer_project.demo.service;

import com.summer_project.demo.dto.ModelPricingRequest;
import com.summer_project.demo.model.ModelPricing;
import com.summer_project.demo.repository.ModelPricingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModelPricingService {
    private final ModelPricingRepository modelPricingRepository;
    public ModelPricingService(ModelPricingRepository modelPricingRepository) {
        this.modelPricingRepository = modelPricingRepository;
    }
    public ModelPricing createPricing(ModelPricingRequest request) {
        validatePricing(request);
        String model = request.model().trim();
        if (request.active()) {
            deactivateActivePricing(model);
        }
        ModelPricing pricing = new ModelPricing(model, request.inputPricePerMillionTokens(), request.outputPricePerMillionTokens(), request.currency().trim().toUpperCase(), request.active(), LocalDateTime.now());
        return modelPricingRepository.save(pricing);
    }
    public List<ModelPricing> getAllPricing() {
        return modelPricingRepository.findAll();
    }
    public ModelPricing updatePricing(String id, ModelPricingRequest request) {
        validatePricing(request);
        ModelPricing oldPricing = modelPricingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pricing not found: " + id));
        String model = request.model().trim();
        if (!oldPricing.getModel().equals(model)) {
            throw new IllegalArgumentException("Model cannot be changed during price update");
        }
        oldPricing.setActive(false);
        modelPricingRepository.save(oldPricing);
        if (request.active()) {
            deactivateActivePricing(model);
        }
        ModelPricing newPricing = new ModelPricing(model, request.inputPricePerMillionTokens(), request.outputPricePerMillionTokens(), request.currency().trim().toUpperCase(), request.active(), LocalDateTime.now());
        return modelPricingRepository.save(newPricing);
    }
    private void validatePricing(ModelPricingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pricing request is required");
        }
        if (request.model() == null || request.model().isBlank()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (request.inputPricePerMillionTokens() == null || request.outputPricePerMillionTokens() == null || request.inputPricePerMillionTokens().compareTo(BigDecimal.ZERO) < 0 || request.outputPricePerMillionTokens().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prices must be non-negative");
        }
        if (request.currency() == null || request.currency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }
    private void deactivateActivePricing(String model) {
        List<ModelPricing> activePricings = modelPricingRepository.findAllByModelAndActiveTrue(model);
        for (ModelPricing pricing : activePricings) {
            pricing.setActive(false);
            modelPricingRepository.save(pricing);
        }
    }
}