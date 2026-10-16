package com.summer_project.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "plans")
public class Plan {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String billingPeriod;
    private long maxRequests;
    private long maxTokens;
    private boolean active;
    private LocalDateTime createdAt;
    public Plan(){}
    public Plan(String name, String description, BigDecimal price, String currency, String billingPeriod, long maxRequests, long maxTokens, boolean active, LocalDateTime createdAt){
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.billingPeriod = billingPeriod;
        this.maxRequests = maxRequests;
        this.maxTokens = maxTokens;
        this.active = active;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public Long getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(Long maxRequests) {
        this.maxRequests = maxRequests;
    }

    public Long getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Long maxTokens) {
        this.maxTokens = maxTokens;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
