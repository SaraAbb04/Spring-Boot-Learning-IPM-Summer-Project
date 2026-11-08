package com.summer_project.demo.gateway;

public record GatewayVerificationResult(boolean successful, String transactionId, String message) {
}
