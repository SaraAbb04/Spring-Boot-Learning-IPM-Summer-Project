package com.summer_project.demo.service;

import com.summer_project.demo.dto.CostCalculationResult;
import com.summer_project.demo.model.ModelPricing;
import com.summer_project.demo.repository.ModelPricingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CostCalculationService {
    private static final BigDecimal ONE_MILLION = new BigDecimal("1000000");
    private final ModelPricingRepository modelPricingRepository;
    public CostCalculationService(ModelPricingRepository modelPricingRepository) {
        this.modelPricingRepository = modelPricingRepository;
    }
    public CostCalculationResult calculateCost(String model, long inputTokens, long outputTokens) {
        if (inputTokens < 0 || outputTokens < 0) {
            throw new IllegalArgumentException("Token counts cannot be negative");
        }
        ModelPricing pricing = modelPricingRepository.findByModelAndActiveTrue(model).orElseThrow(() -> new IllegalStateException("No active pricing found for model: " + model));
        BigDecimal inputCost = pricing.getInputPricePerMillionTokens().multiply(BigDecimal.valueOf(inputTokens)).divide(ONE_MILLION, 12, RoundingMode.HALF_UP);
        BigDecimal outputCost = pricing.getOutputPricePerMillionTokens().multiply(BigDecimal.valueOf(outputTokens)).divide(ONE_MILLION, 12, RoundingMode.HALF_UP);
        BigDecimal totalCost = inputCost.add(outputCost);
        return new CostCalculationResult(totalCost, pricing.getCurrency(), pricing.getId());
    }
}
