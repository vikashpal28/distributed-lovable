package com.distributed_lovable_clone.intelligence_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
 // CRITICAL for composite keys
public class ChatSessionId implements Serializable {
    private Long projectId;
    private Long userId;
}

// composite key contains two or more primary key of any database