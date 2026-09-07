package com.summer_project.demo.exception;

public class UnauthorizedTicketAccessException extends RuntimeException {
    public UnauthorizedTicketAccessException(String message) {
        super(message);
    }
}
