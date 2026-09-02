package com.summer_project.demo.service;

import com.summer_project.demo.dto.CreateTicketRequest;
import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.model.TicketStatus;
import com.summer_project.demo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }
    public Ticket creatTicket(String email, CreateTicketRequest request){
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(email);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}
