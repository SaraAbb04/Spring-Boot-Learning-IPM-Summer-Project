package com.summer_project.demo.service;

import com.summer_project.demo.ai.dto.UsageSummaryResponse;
import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.repository.AiUsageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminAiUsageService {
    private final AiUsageRepository aiUsageRepository;
    private final MongoTemplate mongoTemplate;
    public AdminAiUsageService(AiUsageRepository aiUsageRepository, MongoTemplate mongoTemplate) {
        this.aiUsageRepository = aiUsageRepository;
        this.mongoTemplate = mongoTemplate;
    }
    // 1. Get all usage records with optional filters
    public Page<AiUsage> getAllUsage(String userId, String model, String status, int page, int size) {
        validatePagination(page, size);
        List<Criteria> criteriaList = new ArrayList<>();
        if (userId != null && !userId.isBlank()) {
            criteriaList.add(Criteria.where("userId").is(userId));
        }
        if (model != null && !model.isBlank()) {
            criteriaList.add(Criteria.where("model").is(model));
        }
        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(status));
        }
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestTime"));
        query.with(pageable);
        List<AiUsage> usages = mongoTemplate.find(query, AiUsage.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), AiUsage.class);
        return new PageImpl<>(usages, pageable, total);
    }
    // 2. Get usage records of a specific user
    public Page<AiUsage> getUserUsage(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestTime"));
        return aiUsageRepository.findByUserId(userId, pageable);
    }
    // 3. Get usage summary of a specific user
    public UsageSummaryResponse getUserUsageSummary(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        List<UsageSummaryResponse> summaries = aiUsageRepository.getUsageSummaryByUserId(userId);
        if (summaries.isEmpty()) {
            return new UsageSummaryResponse(0, 0, 0, 0, BigDecimal.ZERO, 0);
        }
        return summaries.get(0);
    }
    // 4. Get one usage record by ID
    public AiUsage getUsageById(String usageId) {
        if (usageId == null || usageId.isBlank()) {
            throw new IllegalArgumentException("Usage ID is required");
        }
        return aiUsageRepository.findById(usageId).orElseThrow(() -> new IllegalArgumentException("Usage record not found"));
    }
    // 5. Validate pagination values
    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }
}