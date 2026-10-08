package com.summer_project.demo.ai;

import com.summer_project.demo.ai.dto.ChatRequest;
import com.summer_project.demo.ai.dto.ChatResponse;

public interface AiProvider {
    ChatResponse chat(ChatRequest request);
}
