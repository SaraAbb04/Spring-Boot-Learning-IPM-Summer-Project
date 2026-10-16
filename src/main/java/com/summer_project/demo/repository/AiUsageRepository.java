package com.summer_project.demo.repository;

import com.summer_project.demo.ai.dto.UsageSummaryResponse;
import com.summer_project.demo.model.AiUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AiUsageRepository  extends MongoRepository<AiUsage, String> {
    Page<AiUsage> findByUserId(String userId, Pageable pageable);
    List<AiUsage> findByConversationId(String conversationId);
    Page<AiUsage> findByUserIdAndConversationId(String userId, String conversationId, Pageable pageable);
    @Aggregation(pipeline = {"{ '$match': { 'userId': ?0, 'status': 'SUCCESS' } }", "{ '$group': { " + "'_id': null, " + "'totalRequests': { '$sum': 1 }, " + "'totalInputTokens': { '$sum': '$inputTokens' }, " + "'totalOutputTokens': { '$sum': '$outputTokens' }, " + "'totalTokens': { '$sum': '$totalTokens' }, " + "'totalCost': { '$sum': '$cost' }, " + "'requestsWithoutCost': { '$sum': { '$cond': [ { '$eq': [ { '$ifNull': [ '$cost', null ] }, null ] }, 1, 0 ] } } " + "} }", "{ '$project': { " + "'_id': 0, " + "'totalRequests': 1, " + "'totalInputTokens': 1, " + "'totalOutputTokens': 1, " + "'totalTokens': 1, " + "'totalCost': 1, " + "'requestsWithoutCost': 1 " + "} }"})
    List<UsageSummaryResponse> getUsageSummaryByUserId(String userId);
}
