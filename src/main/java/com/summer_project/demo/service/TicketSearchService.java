package com.summer_project.demo.service;

import com.summer_project.demo.model.Ticket;
import com.summer_project.demo.model.TicketPriority;
import com.summer_project.demo.model.TicketStatus;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketSearchService {
    private final MongoTemplate mongoTemplate;
    public TicketSearchService(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    public Page<Ticket> searchTicket(int page, int size, String search, TicketStatus status, TicketPriority priority, String sortBy, String direction){
        List<Criteria> criteriaList = new ArrayList<>();
        if(search != null && !search.isBlank()){
            Criteria searchCriteria = new Criteria().orOperator(Criteria.where("title").regex(search, "i"), Criteria.where("description").regex(search, "i"));
            criteriaList.add(searchCriteria);
        }
        if(status != null){
            criteriaList.add(Criteria.where("status").is(status));
        }
        if(priority != null){
            criteriaList.add(Criteria.where("priority").is(priority));
        }
        Query query = new Query();
        if(!criteriaList.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")? Sort.Direction.ASC: Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        long total = mongoTemplate.count(query, Ticket.class);
        query.with(pageable);
        List<Ticket> tickets = mongoTemplate.find(query, Ticket.class);
        return new PageImpl<>(tickets, pageable, total);
    }
}
