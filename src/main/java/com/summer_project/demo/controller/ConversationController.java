package com.summer_project.demo.controller;

import com.summer_project.demo.model.Conversation;
import com.summer_project.demo.service.ConversationService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping
    public Page<Conversation> getMyConversations(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return conversationService.getMyConversations(authentication, page, size);
    }
    @GetMapping("/{conversationId}")
    public Conversation getMyConversation(Authentication authentication, @PathVariable String conversationId) {
        return conversationService.getMyConversation(authentication, conversationId);
    }
}
