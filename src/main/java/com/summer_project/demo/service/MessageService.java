package com.summer_project.demo.service;

import com.summer_project.demo.ai.AiService;
import com.summer_project.demo.ai.dto.ChatMessage;
import com.summer_project.demo.ai.dto.ChatRequest;
import com.summer_project.demo.ai.dto.ChatResponse;
import com.summer_project.demo.model.AiUsage;
import com.summer_project.demo.model.Conversation;
import com.summer_project.demo.model.Message;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.AiUsageRepository;
import com.summer_project.demo.repository.ConversationRepository;
import com.summer_project.demo.repository.MessageRepository;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final AiUsageRepository aiUsageRepository;
    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository, AiService aiService, AiUsageRepository aiUsageRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
        this.aiUsageRepository = aiUsageRepository;
    }
    public Message createUserMessage(Authentication authentication, String conversationId, String content) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, user.getId()).orElseThrow(() -> new RuntimeException("Conversation not found"));
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message(conversation.getId(), user.getId(), "user", content, conversation.getModel(), now);
        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);
        return messageRepository.save(message);
    }
    public Page<Message> getMyMessages(Authentication authentication, String conversationId, int page, int size) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        conversationRepository.findByIdAndUserId(conversationId, user.getId()).orElseThrow(() -> new RuntimeException("Conversation not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return messageRepository.findByConversationIdAndUserId(conversationId, user.getId(), pageable);
    }
    public Message sendMessageToAi(Authentication authentication, String conversationId, String content) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, user.getId()).orElseThrow(() -> new RuntimeException("Conversation not found"));
        LocalDateTime now = LocalDateTime.now();
        Message userMessage = new Message(conversation.getId(), user.getId(), "user", content, conversation.getModel(), now);
        messageRepository.save(userMessage);
        Page<Message> recentMessages = messageRepository.findByConversationIdAndUserId(conversationId, user.getId(), PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<ChatMessage> chatMessages = recentMessages.getContent().stream().sorted(Comparator.comparing(Message::getCreatedAt)).map(message -> new ChatMessage(message.getRole(), message.getContent())).toList();
        ChatRequest request = new ChatRequest(conversation.getModel(), chatMessages);
        ChatResponse response = aiService.chat(request);
        if (response == null || response.choices() == null || response.choices().isEmpty() || response.choices().get(0).message() == null || response.choices().get(0).message().content() == null) {
            throw new RuntimeException("Invalid response from AI service");
        }
        String answer = response.choices().get(0).message().content();
        if (response.usage() != null) {
            AiUsage usage = new AiUsage(user.getId(), conversation.getId(), conversation.getModel(), response.usage().prompt_tokens(), response.usage().completion_tokens(), response.usage().total_tokens(), LocalDateTime.now(), "SUCCESS", null);
            aiUsageRepository.save(usage);
        }
        Message assistantMessage = new Message(conversation.getId(), user.getId(), "assistant", answer, conversation.getModel(), LocalDateTime.now());
        messageRepository.save(assistantMessage);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        return assistantMessage;
    }
}
