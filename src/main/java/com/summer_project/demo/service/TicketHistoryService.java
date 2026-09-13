package com.summer_project.demo.service;

import com.summer_project.demo.model.TicketHistory;
import com.summer_project.demo.model.TicketStatus;
import com.summer_project.demo.repository.TicketHistoryRepository;
import com.summer_project.demo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketHistoryService {
    private final TicketHistoryRepository ticketHistoryRepository;
    public TicketHistoryService(TicketHistoryRepository ticketHistoryRepository){
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    public TicketHistoryRepository getTicketHistoryRepository() {
        return ticketHistoryRepository;
    }
    public TicketHistory createdHistory(String ticketId, TicketStatus status, String message, String performedBy){
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicketId(ticketId);
        ticketHistory.setStatus(status);
        ticketHistory.setMessage(message);
        ticketHistory.setPerformedBy(performedBy);
        ticketHistory.setCreatedAt(LocalDateTime.now());
        return ticketHistoryRepository.save(ticketHistory);
    }
    public List<TicketHistory> getTicketHistory(String ticketId){
        return ticketHistoryRepository.findByTicketId(ticketId);
    }
}
