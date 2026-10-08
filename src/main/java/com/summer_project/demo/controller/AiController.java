package com.summer_project.demo.controller;

import com.summer_project.demo.ai.AiProvider;
import com.summer_project.demo.ai.AiService;
import com.summer_project.demo.ai.dto.ChatRequest;
import com.summer_project.demo.ai.dto.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return aiService.chat(request);
    }
}
