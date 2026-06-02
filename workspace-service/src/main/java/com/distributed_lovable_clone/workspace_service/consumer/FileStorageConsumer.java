package com.distributed_lovable_clone.workspace_service.consumer;

import com.distributed_lovable_clone.common_lib.events.FileStoreRequestEvent;
import com.distributed_lovable_clone.common_lib.events.FileStoreResponseEvents;
import com.distributed_lovable_clone.workspace_service.entity.ProcessedEvent;
import com.distributed_lovable_clone.workspace_service.repository.ProcessedEventRepository;
import com.distributed_lovable_clone.workspace_service.service.ProjectFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageConsumer {

    private final ProjectFileService projectFileService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Transactional
    @KafkaListener(topics = "file-storage-request-event" , groupId = "workspace-group")
    public void consumeFileEvent(FileStoreRequestEvent fileStoreRequestEvent){
        if(processedEventRepository.existsById(fileStoreRequestEvent.sagaId())){
            log.info("File store request event already exists");
            sendResponse(fileStoreRequestEvent , true , null);
            return;
        }

        try {
            log.info("Received file store request event");
            projectFileService.saveFile(fileStoreRequestEvent.projectId(), fileStoreRequestEvent.filePath(), fileStoreRequestEvent.content());
            processedEventRepository.save(new ProcessedEvent(
                    fileStoreRequestEvent.sagaId(),
                    LocalDateTime.now()
            ));
            sendResponse(fileStoreRequestEvent , true, null);
        }
        catch (Exception e){
            log.error("Error while processing file store request event");
            sendResponse(fileStoreRequestEvent , false, e.getMessage());
        }

    }

    public void sendResponse(FileStoreRequestEvent fileStoreRequestEvent, boolean success, String error) {
        FileStoreResponseEvents response = FileStoreResponseEvents.builder()
                .sagaId(fileStoreRequestEvent.sagaId())
                .projectId(fileStoreRequestEvent.projectId())
                .success(success)
                .errorMessage(error)
                .build();
        kafkaTemplate.send("file-storage-response-event", response);
    }
}
