
package com.summer_project.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public record KpiOverviewResponse(UserStatistics users, AiStatistics ai, TokenStatistics tokens, CostStatistics costs, RevenueStatistics revenue) {
    public record UserStatistics(long totalUsers, long adminUsers, long regularUsers, long otherRoles) {}
    public record AiStatistics(long totalRequests, long successfulRequests, long failedRequests, long otherRequests) {}
    public record TokenStatistics(long inputTokens, long outputTokens, long totalTokens) {}
    public record CostStatistics(List<CurrencyAmount> totalCostByCurrency, List<ModelCostStatistics> byModel) {}
    public record ModelCostStatistics(String model, long successfulRequests, List<CurrencyAmount> costByCurrency) {}
    public record RevenueStatistics(List<CurrencyAmount> totalRevenueByCurrency, long successfulPayments, long failedPayments, long pendingPayments, long processingPayments, long otherPayments) {}
    public record CurrencyAmount(String currency, BigDecimal amount) {}
}