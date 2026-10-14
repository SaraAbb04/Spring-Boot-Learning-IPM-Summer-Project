package com.summer_project.demo.exception;

public class PricingNotFoundException extends RuntimeException {
    public PricingNotFoundException(String message) {
        super(message);
    }
}
