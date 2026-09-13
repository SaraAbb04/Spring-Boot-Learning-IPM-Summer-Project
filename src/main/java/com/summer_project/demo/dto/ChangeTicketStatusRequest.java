package com.summer_project.demo.dto;

import com.summer_project.demo.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeTicketStatusRequest {
    @NotNull(message = "Status can't be Empty")
    private TicketStatus ticketStatus;
    public ChangeTicketStatusRequest(){}

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
