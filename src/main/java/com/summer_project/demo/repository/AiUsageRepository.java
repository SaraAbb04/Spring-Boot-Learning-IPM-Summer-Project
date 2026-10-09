package com.summer_project.demo.repository;

import com.summer_project.demo.model.AiUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AiUsageRepository  extends MongoRepository<AiUsage, String> {
    List<AiUsage> findByUserId(String userId);
    List<AiUsage> findByConversationId(String conversationId);
    List<AiUsage> findByUserIdAndConversationId(String userId, String conversationId);
}
