package com.summer_project.demo.dto;

import java.math.BigDecimal;

public record ModelPricingRequest(String model, BigDecimal inputPricePerMillionTokens, BigDecimal outputPricePerMillionTokens, String currency, boolean active) {
}
