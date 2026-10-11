package com.summer_project.demo.controller;

import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.service.AiUsageService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usage")
public class AiUsageController {
    private final AiUsageService aiUsageService;
    public AiUsageController(AiUsageService aiUsageService) {
        this.aiUsageService = aiUsageService;
    }
    @GetMapping
    public Page<AiUsage> getMyUsage(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return aiUsageService.getMyUsage(authentication, page, size);
    }
    @GetMapping("/conversations/{conversationId}")
    public Page<AiUsage> getMyConversationUsage(Authentication authentication, @PathVariable String conversationId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return aiUsageService.getMyConversationUsage(authentication, conversationId, page, size);
    }
}
