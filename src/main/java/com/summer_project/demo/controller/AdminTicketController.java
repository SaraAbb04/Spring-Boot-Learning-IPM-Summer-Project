package com.summer_project.demo.controller;

import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.service.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
