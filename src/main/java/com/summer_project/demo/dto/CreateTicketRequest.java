package com.summer_project.demo.dto;

import com.summer_project.demo.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;

public class CreateTicketRequest {
    @NotBlank(message = "Title can't be empty")
    private String title;
    @NotBlank(message = "Description can't be empty")
    private String description;
    @NotBlank(message = "Priority can't be empty")
    private TicketPriority priority;
    public CreateTicketRequest(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
}
