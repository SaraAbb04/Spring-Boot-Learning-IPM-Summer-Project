package com.summer_project.demo.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatRequest(String model, @JsonProperty("messages")List<ChatMessage> messageList) {
}
