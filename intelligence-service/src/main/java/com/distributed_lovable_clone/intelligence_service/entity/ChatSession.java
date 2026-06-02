package com.distributed_lovable_clone.intelligence_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatSession {

    @EmbeddedId
    ChatSessionId id; //avoid null pointer Exception here

    @CreationTimestamp
            @Column(nullable = false , updatable = false)
    Instant createdAt;
    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft deleted

}
