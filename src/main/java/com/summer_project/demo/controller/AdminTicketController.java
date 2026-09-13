package com.summer_project.demo.controller;

import com.summer_project.demo.dto.AddTicketReplyRequest;
import com.summer_project.demo.dto.ChangeTicketStatusRequest;
import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tickets")
public class AdminTicketController {
    private final TicketService ticketService;
    public AdminTicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @GetMapping
    public List<Ticket> getAllTickets(){
        return ticketService.getAllTickets();
    }
    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable String id){
        return ticketService.getTicketForAdmin(id);
    }
    @PutMapping("/{id}/status")
    public Ticket changeTicketStatus(@PathVariable String id, Authentication authentication, @Valid @RequestBody ChangeTicketStatusRequest request){
        return ticketService.changeTicketStatus(id, request.getTicketStatus(), authentication.getName());
    }
    @PostMapping("/{id}/reply")
    public ResponseEntity<String> addReply(@PathVariable String id, Authentication authentication, @Valid @RequestBody AddTicketReplyRequest request){
        ticketService.addAdminReply(id, request.getMessage(), authentication.getName());
        return ResponseEntity.ok("Reply added successfully!");
    }
}
