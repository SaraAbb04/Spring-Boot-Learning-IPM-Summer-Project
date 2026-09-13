package com.summer_project.demo.repository;

import com.summer_project.demo.model.TicketHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketHistoryRepository extends MongoRepository<TicketHistory, String> {
    List<TicketHistory> findByTicketId(String ticketId);
}
