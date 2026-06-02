package com.distributed_lovable_clone.intelligence_service.dto.chat;



import com.distributed_lovable_clone.common_lib.type.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(

        Long id,
        String content,
        MessageRole role,
        List<ChatEventResponse> events,
        Integer tokenUsed,
        Instant createdAt

) {
}
