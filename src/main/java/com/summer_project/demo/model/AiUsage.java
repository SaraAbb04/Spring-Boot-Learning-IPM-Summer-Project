package com.summer_project.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ai_usage")
public class AiUsage {
    @Id
    private String id;
    private String userId;
    private String conversationId;
    private String model;
    private long inputTokens;
    private long outputTokens;
    private long totalTokens;
    private LocalDateTime requestTime;
    private String status;
    private String errorMessage;
    public AiUsage() {}
    public AiUsage(String userId, String conversationId, String model, long inputTokens, long outputTokens, long totalTokens, LocalDateTime requestTime, String status, String errorMessage) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.model = model;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.totalTokens = totalTokens;
        this.requestTime = requestTime;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(long inputTokens) {
        this.inputTokens = inputTokens;
    }

    public long getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(long outputTokens) {
        this.outputTokens = outputTokens;
    }

    public long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
