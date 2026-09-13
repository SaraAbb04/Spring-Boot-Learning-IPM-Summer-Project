package com.summer_project.demo.controller;

import com.summer_project.demo.dto.CreateTicketRequest;
import com.summer_project.demo.dto.UpdateTicketRequest;
import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.model.TicketHistory;
import com.summer_project.demo.service.TicketHistoryService;
import com.summer_project.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final TicketHistoryService ticketHistoryService;
    public TicketController(TicketService ticketService, TicketHistoryService ticketHistoryService){
        this.ticketService = ticketService;
        this.ticketHistoryService = ticketHistoryService;
    }
    @PostMapping
    public Ticket creatTicket(Authentication authentication, @Valid @RequestBody CreateTicketRequest request){
        String email = authentication.getName();
        return ticketService.creatTicket(email, request);
    }
    @GetMapping
    public List<Ticket> getMyTickets(Authentication authentication){
        String email = authentication.getName();
        return ticketService.getMyTickets(email);
    }
    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable String id, Authentication authentication){
        String email = authentication.getName();
        return ticketService.getTicketById(id, email);
    }
    @PutMapping("/{id}")
    public Ticket updateTicket(@PathVariable String id, Authentication authentication, @Valid @RequestBody UpdateTicketRequest request){
        return ticketService.updateTicket(id, authentication.getName(), request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable String id, Authentication authentication){
        ticketService.deleteTicket(id, authentication.getName());
        return ResponseEntity.ok("Ticket deleted successfully");
    }
    @GetMapping("/{id}/history")
    public List<TicketHistory> getTicketHistory(@PathVariable String id, Authentication authentication){
        ticketService.getTicketById(id, authentication.getName());
        return ticketHistoryService.getTicketHistory(id);
    }
}
