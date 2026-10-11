package com.summer_project.demo.service;

import com.summer_project.demo.ai.dto.UsageSummaryResponse;
import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.AiUsageRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiUsageService {
    private final AiUsageRepository aiUsageRepository;
    private final UserRepository userRepository;
    public AiUsageService(AiUsageRepository aiUsageRepository, UserRepository userRepository) {
        this.aiUsageRepository = aiUsageRepository;
        this.userRepository = userRepository;
    }
    public Page<AiUsage> getMyUsage(Authentication authentication, int page, int size) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestTime")
        );
        return aiUsageRepository.findByUserId(user.getId(), pageable);
    }
    public Page<AiUsage> getMyConversationUsage(Authentication authentication, String conversationId, int page, int size) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestTime"));
        return aiUsageRepository.findByUserIdAndConversationId(user.getId(), conversationId, pageable);
    }
    public UsageSummaryResponse getMyUsageSummary(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        List<UsageSummaryResponse> summaries = aiUsageRepository.getUsageSummaryByUserId(user.getId());
        if (summaries.isEmpty()) {
            return new UsageSummaryResponse(0, 0, 0, 0);
        }
        return summaries.get(0);
    }
}
