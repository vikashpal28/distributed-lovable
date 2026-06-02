package com.distributed_lovable_clone.intelligence_service.llm.advisor;


import com.distributed_lovable_clone.common_lib.dto.FileNode;
import com.distributed_lovable_clone.intelligence_service.client.WorkspaceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FileTreeContentAdvisor implements StreamAdvisor {

    private final WorkspaceClient workspaceClient;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Map<String , Object> context = chatClientRequest.context();
       Long projectId = Long.parseLong(context.getOrDefault("projectId" , 0).toString());
      ChatClientRequest augmentChatClientrequest = augmentRequestWithFileTree(chatClientRequest , projectId);
        return streamAdvisorChain.nextStream(augmentChatClientrequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request , Long projectId){
        List<Message> incomingMessage = request.prompt().getInstructions();
        Message systemMessage = incomingMessage.stream()
                .filter(m -> m.getMessageType() == MessageType.SYSTEM)
                .findFirst()
                .orElse(null);

        List<Message> userMessages = incomingMessage.stream()
                .filter(m -> m.getMessageType() != MessageType.SYSTEM)
                .toList();

        List<Message> allMessage = new ArrayList<>();

        if(systemMessage != null){
            allMessage.add(systemMessage);
        }

        List<FileNode> fileTree = workspaceClient.getFileTree(projectId).files();
        String fileTreeContent = "\n\n---- FILE TREE ----\n"+fileTree.toString();
        allMessage.add(new SystemMessage(fileTreeContent));

        allMessage.addAll(userMessages);

        return request.mutate()
                .prompt(new Prompt(allMessage , request.prompt().getOptions()))
                .build();
    }

    @Override
    public String getName() {
        return "FileTreeContentAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
