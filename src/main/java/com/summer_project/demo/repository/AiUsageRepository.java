package com.summer_project.demo.repository;

import com.summer_project.demo.model.AiUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AiUsageRepository  extends MongoRepository<AiUsage, String> {
    Page<AiUsage> findByUserId(String userId, Pageable pageable);
    List<AiUsage> findByConversationId(String conversationId);
    List<AiUsage> findByUserIdAndConversationId(String userId, String conversationId);
}
