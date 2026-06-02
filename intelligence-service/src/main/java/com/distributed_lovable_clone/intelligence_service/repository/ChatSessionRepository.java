package com.distributed_lovable_clone.intelligence_service.repository;


import com.distributed_lovable_clone.intelligence_service.entity.ChatSession;
import com.distributed_lovable_clone.intelligence_service.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {


}
