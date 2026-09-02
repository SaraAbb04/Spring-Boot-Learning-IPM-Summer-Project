package com.summer_project.demo.controller;

import com.summer_project.demo.dto.CreateTicketRequest;
import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @PostMapping
    public Ticket creatTicket(Authentication authentication, @Valid @RequestBody CreateTicketRequest request){
        String email = authentication.getName();
        return ticketService.creatTicket(email, request);
    }
}
