package com.summer_project.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class AddTicketReplyRequest {
    @NotBlank(message = "Massage can't be empty!")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
