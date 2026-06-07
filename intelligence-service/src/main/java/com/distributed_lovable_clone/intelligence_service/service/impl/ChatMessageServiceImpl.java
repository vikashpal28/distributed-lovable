package com.distributed_lovable_clone.intelligence_service.service.impl;


import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.intelligence_service.entity.ChatMessage;
import com.distributed_lovable_clone.intelligence_service.entity.ChatSession;
import com.distributed_lovable_clone.intelligence_service.entity.ChatSessionId;
import com.distributed_lovable_clone.intelligence_service.mapper.ChatMapper;
import com.distributed_lovable_clone.intelligence_service.repository.ChatMessageRepository;
import com.distributed_lovable_clone.intelligence_service.repository.ChatSessionRepository;
import com.distributed_lovable_clone.intelligence_service.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<com.distributed_lovable_clone.intelligence_service.dto.chat.ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(userId , projectId)
        );

    List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
