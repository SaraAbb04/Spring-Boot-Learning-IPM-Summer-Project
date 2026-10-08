package com.summer_project.demo.controller;

import com.summer_project.demo.model.Conversation;
import com.summer_project.demo.service.ConversationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }
    @PostMapping
    public Conversation createConversation(Authentication authentication, @RequestParam String title, @RequestParam String model) {
        return conversationService.createConversation(authentication, title, model);
    }
}
