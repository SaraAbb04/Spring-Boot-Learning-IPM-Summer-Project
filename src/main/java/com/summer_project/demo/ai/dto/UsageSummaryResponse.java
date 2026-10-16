package com.summer_project.demo.ai.dto;

import java.math.BigDecimal;

public record UsageSummaryResponse(long totalRequests, long totalInputTokens, long totalOutputTokens, long totalTokens, BigDecimal totalCost, long requestsWithoutCost) {}