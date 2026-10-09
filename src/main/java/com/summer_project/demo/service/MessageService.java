package com.summer_project.demo.service;

import com.summer_project.demo.model.Conversation;
import com.summer_project.demo.model.Message;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.ConversationRepository;
import com.summer_project.demo.repository.MessageRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }
    public Message createUserMessage(Authentication authentication, String conversationId, String content) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, user.getId()).orElseThrow(() -> new RuntimeException("Conversation not found"));
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message(conversation.getId(), user.getId(), "user", content, conversation.getModel(), now);
        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);
        return messageRepository.save(message);
    }
    public Page<Message> getMyMessages(Authentication authentication, String conversationId, int page, int size) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        conversationRepository.findByIdAndUserId(conversationId, user.getId()).orElseThrow(() -> new RuntimeException("Conversation not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return messageRepository.findByConversationIdAndUserId(conversationId, user.getId(), pageable);
    }
}
