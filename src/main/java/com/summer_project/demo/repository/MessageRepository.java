package com.summer_project.demo.repository;

import com.summer_project.demo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findByConversationIdAndUserId(String conversationId, String userId, Pageable pageable);
}
