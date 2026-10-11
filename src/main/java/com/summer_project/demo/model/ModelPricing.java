package com.summer_project.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "model_pricing")
public class ModelPricing {
    @Id
    private String id;
    private String model;
    private BigDecimal inputPricePerMillionTokens;
    private BigDecimal outputPricePerMillionTokens;
    private String currency;
    private boolean active;
    private LocalDateTime updatedAt;
    public ModelPricing() {}
    public ModelPricing(String model, BigDecimal inputPricePerMillionTokens, BigDecimal outputPricePerMillionTokens, String currency, boolean active, LocalDateTime updatedAt) {
        this.model = model;
        this.inputPricePerMillionTokens = inputPricePerMillionTokens;
        this.outputPricePerMillionTokens = outputPricePerMillionTokens;
        this.currency = currency;
        this.active = active;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getInputPricePerMillionTokens() {
        return inputPricePerMillionTokens;
    }

    public void setInputPricePerMillionTokens(BigDecimal inputPricePerMillionTokens) {
        this.inputPricePerMillionTokens = inputPricePerMillionTokens;
    }

    public BigDecimal getOutputPricePerMillionTokens() {
        return outputPricePerMillionTokens;
    }

    public void setOutputPricePerMillionTokens(BigDecimal outputPricePerMillionTokens) {
        this.outputPricePerMillionTokens = outputPricePerMillionTokens;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
