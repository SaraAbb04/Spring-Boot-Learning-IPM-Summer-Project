package com.summer_project.demo.ai.dto;

import java.util.List;

public record ChatResponse(String id, String object, long created, String model, List<Choice> choices, Usage usage) {
    public record Choice(int index, String finish_reason, Message message) {

    }
    public record Message(String role, String content) {

    }
    public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {

    }
}