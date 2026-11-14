package com.summer_project.demo.service;

public final class PaginationValidator {
    private PaginationValidator() {}

    public static void validate(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page number cannot be negative");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
    }
}
