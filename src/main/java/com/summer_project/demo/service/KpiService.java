package com.summer_project.demo.service;

import com.summer_project.demo.dto.KpiOverviewResponse;
import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.model.Payment;
import com.summer_project.demo.model.PaymentStatus;
import com.summer_project.demo.model.Role;
import com.summer_project.demo.model.User;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class KpiService {
    private final MongoTemplate mongoTemplate;
    public KpiService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public KpiOverviewResponse getOverview() {
        return new KpiOverviewResponse(getUserStatistics(), getAiStatistics(), getTokenStatistics(), getCostStatistics(), getRevenueStatistics());
    }
    // 1. User Statistics
    private KpiOverviewResponse.UserStatistics getUserStatistics() {
        long totalUsers = mongoTemplate.count(new Query(), User.class);
        long adminUsers = mongoTemplate.count(Query.query(Criteria.where("role").is(Role.ADMIN)), User.class);
        long regularUsers = mongoTemplate.count(Query.query(Criteria.where("role").is(Role.USER)), User.class);
        long otherRoles = totalUsers - adminUsers - regularUsers;
        return new KpiOverviewResponse.UserStatistics(totalUsers, adminUsers, regularUsers, otherRoles);
    }
    // 2. AI Statistics
    private KpiOverviewResponse.AiStatistics getAiStatistics() {
        List<AiUsage> usages = mongoTemplate.findAll(AiUsage.class);
        long totalRequests = usages.size();
        long successfulRequests = usages.stream().filter(usage -> isStatus(usage.getStatus(), "SUCCESS")).count();
        long failedRequests = usages.stream().filter(usage -> isStatus(usage.getStatus(), "FAILED")).count();
        long otherRequests = totalRequests - successfulRequests - failedRequests;
        return new KpiOverviewResponse.AiStatistics(totalRequests, successfulRequests, failedRequests, otherRequests);
    }
    // 3. Token Statistics
    // Only successful requests are included.
    private KpiOverviewResponse.TokenStatistics getTokenStatistics() {
        List<AiUsage> usages = mongoTemplate.findAll(AiUsage.class);
        long inputTokens = 0;
        long outputTokens = 0;
        long totalTokens = 0;
        for (AiUsage usage : usages) {
            if (!isStatus(usage.getStatus(), "SUCCESS")) {
                continue;
            }
            inputTokens += usage.getInputTokens();
            outputTokens += usage.getOutputTokens();
            totalTokens += usage.getTotalTokens();
        }
        return new KpiOverviewResponse.TokenStatistics(inputTokens, outputTokens, totalTokens);
    }
    // 4. Cost Statistics
    // Only successful requests are included.
    private KpiOverviewResponse.CostStatistics getCostStatistics() {
        List<AiUsage> usages = mongoTemplate.findAll(AiUsage.class);
        Map<String, BigDecimal> totalCostByCurrency = new TreeMap<>();
        Map<String, ModelCostData> modelCosts = new TreeMap<>();
        for (AiUsage usage : usages) {
            if (!isStatus(usage.getStatus(), "SUCCESS")) {
                continue;
            }
            String currency = normalizeCurrency(usage.getCurrency());
            BigDecimal cost = usage.getCost() != null ? usage.getCost() : BigDecimal.ZERO;
            totalCostByCurrency.merge(currency, cost, BigDecimal::add);
            String model = usage.getModel() != null && !usage.getModel().isBlank() ? usage.getModel() : "UNKNOWN";
            ModelCostData data = modelCosts.computeIfAbsent(model, key -> new ModelCostData());
            data.successfulRequests++;
            data.costByCurrency.merge(currency, cost, BigDecimal::add);
        }
        List<KpiOverviewResponse.CurrencyAmount> totalCosts = toCurrencyAmounts(totalCostByCurrency);
        List<KpiOverviewResponse.ModelCostStatistics> byModel = new ArrayList<>();
        for (Map.Entry<String, ModelCostData> entry : modelCosts.entrySet()) {
            ModelCostData data = entry.getValue();
            byModel.add(new KpiOverviewResponse.ModelCostStatistics(entry.getKey(), data.successfulRequests, toCurrencyAmounts(data.costByCurrency)));
        }
        return new KpiOverviewResponse.CostStatistics(totalCosts, byModel);
    }
    // 5. Revenue Statistics
    // Only successful payments contribute to revenue.
    private KpiOverviewResponse.RevenueStatistics getRevenueStatistics() {
        List<Payment> payments = mongoTemplate.findAll(Payment.class);
        long successfulPayments = 0;
        long failedPayments = 0;
        long pendingPayments = 0;
        long processingPayments = 0;
        long otherPayments = 0;
        Map<String, BigDecimal> revenueByCurrency = new TreeMap<>();
        for (Payment payment : payments) {
            String status = payment.getStatus();
            if (isStatus(status, PaymentStatus.SUCCESS.name())) {
                successfulPayments++;
                String currency = normalizeCurrency(payment.getCurrency());
                BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
                revenueByCurrency.merge(currency, amount, BigDecimal::add);
            } else if (isStatus(status, PaymentStatus.FAILED.name())) {
                failedPayments++;

            } else if (isStatus(status, PaymentStatus.PENDING.name())) {
                pendingPayments++;

            } else if (isStatus(status, PaymentStatus.PROCESSING.name())) {
                processingPayments++;

            } else {
                otherPayments++;
            }
        }
        return new KpiOverviewResponse.RevenueStatistics(toCurrencyAmounts(revenueByCurrency), successfulPayments, failedPayments, pendingPayments, processingPayments, otherPayments);
    }
    // Helper methods
    private boolean isStatus(String actual, String expected) {
        return actual != null && actual.equalsIgnoreCase(expected);
    }
    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "UNKNOWN";
        }
        return currency.toUpperCase();
    }
    private List<KpiOverviewResponse.CurrencyAmount> toCurrencyAmounts(Map<String, BigDecimal> amounts) {
        List<KpiOverviewResponse.CurrencyAmount> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : amounts.entrySet()) {
            result.add(new KpiOverviewResponse.CurrencyAmount(entry.getKey(), entry.getValue()));
        }
        return result;
    }
    private static class ModelCostData {
        private long successfulRequests;
        private final Map<String, BigDecimal> costByCurrency = new TreeMap<>();
    }
}