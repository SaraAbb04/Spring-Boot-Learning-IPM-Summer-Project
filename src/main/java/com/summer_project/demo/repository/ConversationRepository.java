package com.summer_project.demo.repository;

import com.summer_project.demo.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Page<Conversation> findByUserId(String userId, Pageable pageable);
    Optional<Conversation> findByIdAndUserId(String id, String userId);
}
