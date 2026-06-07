package com.distributed_lovable_clone.intelligence_service.controller;


import com.distributed_lovable_clone.intelligence_service.dto.chat.ChatRequest;
import com.distributed_lovable_clone.intelligence_service.service.AiGenerationService;
import com.distributed_lovable_clone.intelligence_service.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final AiGenerationService aiGenerationService;
    private final ChatMessageService chatMessageService;

    @PostMapping(value = "/stream" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest chatRequest){
      return aiGenerationService.streamResponse(chatRequest.message() , chatRequest.projectId())
              .map(data -> ServerSentEvent.<String>builder()
                              .data(String.valueOf(data))
                              .build()
                      );
    }

    @GetMapping("/projects/{projectId}/chat")
    public ResponseEntity<List<com.distributed_lovable_clone.intelligence_service.dto.chat.ChatResponse>> getChatHistory(@PathVariable Long projectId){
        return ResponseEntity.ok(chatMessageService.getProjectChatHistory(projectId));

    }

}
