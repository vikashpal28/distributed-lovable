package com.distributed_lovable_clone.workspace_service.repository;

import com.distributed_lovable_clone.workspace_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent , String> {
}
