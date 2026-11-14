package com.summer_project.demo.controller;

import com.summer_project.demo.model.Message;
import com.summer_project.demo.service.MessageService;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    @PostMapping
    public Message sendMessageToAi(Authentication authentication, @PathVariable String conversationId, @Valid @RequestBody CreateMessageRequest request) {
        return messageService.sendMessageToAi(authentication, conversationId, request.content());
    }
    public record CreateMessageRequest(@NotBlank(message = "Message content is required") @Size(max = 12000, message = "Message content cannot exceed 12000 characters") String content) {}
    @GetMapping
    public Page<Message> getMyMessages(Authentication authentication, @PathVariable String conversationId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return messageService.getMyMessages(authentication, conversationId, page, size);
    }
}
