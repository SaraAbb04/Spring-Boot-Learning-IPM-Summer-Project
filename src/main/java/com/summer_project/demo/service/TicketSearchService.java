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
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class TicketSearchService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "title", "status", "priority");
    private final MongoTemplate mongoTemplate;

    public TicketSearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Ticket> searchTicket(int page, int size, String search, TicketStatus status,
                                     TicketPriority priority, String sortBy, String direction) {
        validatePagination(page, size);
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy. Allowed values: createdAt, title, status, priority");
        }
        if (direction == null || !(direction.equalsIgnoreCase("asc") || direction.equalsIgnoreCase("desc"))) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        if (search != null && search.length() > 100) {
            throw new IllegalArgumentException("Search text cannot exceed 100 characters");
        }

        List<Criteria> criteriaList = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            String literalSearch = Pattern.quote(search.trim());
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(literalSearch, "i"),
                    Criteria.where("description").regex(literalSearch, "i"));
            criteriaList.add(searchCriteria);
        }
        if (status != null) criteriaList.add(Criteria.where("status").is(status));
        if (priority != null) criteriaList.add(Criteria.where("priority").is(priority));

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        long total = mongoTemplate.count(query, Ticket.class);
        query.with(pageable);
        List<Ticket> tickets = mongoTemplate.find(query, Ticket.class);
        return new PageImpl<>(tickets, pageable, total);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page number cannot be negative");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
    }
}
