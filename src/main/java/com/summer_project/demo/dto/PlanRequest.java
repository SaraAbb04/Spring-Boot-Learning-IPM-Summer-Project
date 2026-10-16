package com.summer_project.demo.dto;

import java.math.BigDecimal;

public record PlanRequest(String name, String description, BigDecimal price, String currency, String billingPeriod, long maxRequests, long maxTokens, boolean active) {
}
