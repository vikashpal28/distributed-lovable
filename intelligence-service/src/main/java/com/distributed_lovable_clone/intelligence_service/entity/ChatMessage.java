package com.distributed_lovable_clone.intelligence_service.entity;


import com.distributed_lovable_clone.common_lib.type.MessageRole;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id" , referencedColumnName = "projectId" , nullable = false),
            @JoinColumn(name = "user_id" , referencedColumnName = "userId" , nullable = false)
    })
    ChatSession chatSession;

    @Column(columnDefinition = "text")
    String content; // null unless user role
   // String toolCalls; //JSON array of tool calls;

    @Enumerated(value = EnumType.STRING)
            @Column(nullable = false)
    MessageRole role;

    @OneToMany(mappedBy = "chatMessage",fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @OrderBy("sequenceOrder  ASC")
    @JsonManagedReference
    List<ChatEvent> events; // null unless Assistant role


    Integer tokenUsed = 0;

    @CreationTimestamp
    Instant createdAt;
}
