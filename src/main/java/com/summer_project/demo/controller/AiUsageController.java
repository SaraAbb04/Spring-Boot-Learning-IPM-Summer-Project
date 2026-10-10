package com.summer_project.demo.controller;

import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.service.AiUsageService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
