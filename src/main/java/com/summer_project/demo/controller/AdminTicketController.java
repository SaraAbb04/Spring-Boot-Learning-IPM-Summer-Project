package com.summer_project.demo.controller;

import com.summer_project.demo.dto.AddTicketReplyRequest;
import com.summer_project.demo.dto.ChangeTicketStatusRequest;
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
@RequestMapping("/admin/tickets")
public class AdminTicketController {
    private final TicketService ticketService;
    private final TicketHistoryService ticketHistoryService;
    public AdminTicketController(TicketService ticketService, TicketHistoryService ticketHistoryService){
        this.ticketService = ticketService;
        this.ticketHistoryService = ticketHistoryService;
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
    @GetMapping("/{id}/history")
    public List<TicketHistory> getTicketHistory(@PathVariable String id){
        ticketService.getTicketForAdmin(id);
        return ticketHistoryService.getTicketHistory(id);
    }
}
