package com.summer_project.demo.ai;

import com.summer_project.demo.ai.dto.ChatRequest;
import com.summer_project.demo.ai.dto.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TeamAiProvider implements AiProvider{
    private final RestClient restClient;
    public TeamAiProvider(RestClient restClient){
        this.restClient = restClient;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        return restClient.post().uri("/chat/completions").body(request).retrieve().body(ChatResponse.class);
    }
}
