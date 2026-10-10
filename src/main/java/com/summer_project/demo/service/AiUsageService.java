package com.summer_project.demo.service;

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
}
