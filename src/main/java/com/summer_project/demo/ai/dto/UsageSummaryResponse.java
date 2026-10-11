package com.summer_project.demo.ai.dto;

public record UsageSummaryResponse(long totalRequests, long totalInputTokens, long totalOutputTokens, long totalTokens) {
}
