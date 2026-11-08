package com.summer_project.demo.gateway;

import java.math.BigDecimal;

public record GatewayPaymentRequest(String paymentId, BigDecimal amount, String currency, String callbackUrl, String description) {
}
