package com.distributed_lovable_clone.intelligence_service.dto.chat;


import com.distributed_lovable_clone.common_lib.type.ChatEventType;

public record ChatEventResponse(
        Long id,
        Integer sequenceOrder,
        ChatEventType type,
        String content,
        String filePath,
        String metaData
) {
}
