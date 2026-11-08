package com.summer_project.demo.gateway;

public record GatewayVerificationRequest(String paymentId, String gatewayReference) {
}
