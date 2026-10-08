package com.summer_project.demo.ai;

import com.summer_project.demo.ai.dto.ChatRequest;
import com.summer_project.demo.ai.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final AiProvider aiProvider;
    public AiService(AiProvider aiProvider){
        this.aiProvider = aiProvider;
    }
    public ChatResponse chat(ChatRequest request){
        return aiProvider.chat(request);
    }
}
