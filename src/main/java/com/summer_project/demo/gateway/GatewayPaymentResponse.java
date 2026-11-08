package com.summer_project.demo.gateway;

public record GatewayPaymentResponse(String redirectUrl, String gatewayReference) {
}
