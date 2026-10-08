package com.summer_project.demo.service;

import com.summer_project.demo.model.Conversation;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.ConversationRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    public ConversationService(ConversationRepository conversationRepository, UserRepository userRepository){
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }
    public Conversation createConversation(Authentication authentication, String title, String model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        Conversation conversation = new Conversation(user.getId(), title, model, now, now);
        return conversationRepository.save(conversation);
    }
}
