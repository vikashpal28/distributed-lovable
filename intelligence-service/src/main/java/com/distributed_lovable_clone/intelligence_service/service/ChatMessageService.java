package com.distributed_lovable_clone.intelligence_service.service;



import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

public interface ChatMessageService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}
