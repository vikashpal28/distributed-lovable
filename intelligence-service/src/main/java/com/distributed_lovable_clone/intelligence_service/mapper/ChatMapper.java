package com.distributed_lovable_clone.intelligence_service.mapper;


import com.distributed_lovable_clone.intelligence_service.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<com.distributed_lovable_clone.intelligence_service.dto.chat.ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);

}
