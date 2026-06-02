package com.distributed_lovable_clone.intelligence_service.consumer;

import com.distributed_lovable_clone.common_lib.events.FileStoreResponseEvents;
import com.distributed_lovable_clone.common_lib.type.ChatEventStatus;
import com.distributed_lovable_clone.intelligence_service.repository.ChatEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntelligenceSagaResponseHandler {
    private final ChatEventRepository chatEventRepository;

    @Transactional
    @KafkaListener(topics = "file-stored-responses" , groupId = "intelligence-group")
    public void handleSagaResponse(FileStoreResponseEvents res){
        chatEventRepository.findBySagaId(res.sagaId()).ifPresent(event ->{
            if(!ChatEventStatus.PENDING.equals(event.getStatus())){
                log.info("Response for Saga {} already handled , Skipping" , res.sagaId());
                return;
            }
            if(res.success()){
                event.setStatus(ChatEventStatus.CONFIRMED);
                log.info("Response for Saga {} handled ", res.sagaId());
            }
            else{
                log.warn("Response for Saga {} handled ", res.sagaId());
                event.setStatus(ChatEventStatus.FAILED);
            }
        });
    }
}
