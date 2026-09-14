package com.summer_project.demo.controller;

import com.summer_project.demo.dto.AddTicketReplyRequest;
import com.summer_project.demo.dto.ChangeTicketStatusRequest;
import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.model.TicketHistory;
import com.summer_project.demo.model.TicketPriority;
import com.summer_project.demo.model.TicketStatus;
import com.summer_project.demo.service.TicketHistoryService;
import com.summer_project.demo.service.TicketSearchService;
import com.summer_project.demo.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tickets")
public class AdminTicketController {
    private final TicketService ticketService;
    private final TicketHistoryService ticketHistoryService;
    private final TicketSearchService ticketSearchService;
    public AdminTicketController(TicketService ticketService, TicketHistoryService ticketHistoryService, TicketSearchService ticketSearchService){
        this.ticketService = ticketService;
        this.ticketHistoryService = ticketHistoryService;
        this.ticketSearchService = ticketSearchService;
    }
    @GetMapping
    public Page<Ticket> getAllTickets(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size,
                                      @RequestParam(required = false)String search, @RequestParam(required = false)TicketStatus status,
                                      @RequestParam(required = false)TicketPriority priority, @RequestParam(defaultValue = "createdAt")String sortBy,
                                      @RequestParam(defaultValue = "desc")String direction){
        return ticketSearchService.searchTicket(page, size, search, status, priority, sortBy, direction);
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
