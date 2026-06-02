package com.distributed_lovable_clone.intelligence_service.entity;


import com.distributed_lovable_clone.common_lib.type.ChatEventStatus;
import com.distributed_lovable_clone.common_lib.type.ChatEventType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "chat_events")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(nullable = false)
            @JsonBackReference
    ChatMessage chatMessage;

    @Column(nullable = false)
    Integer sequenceOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChatEventType type;

    @Column(columnDefinition = "text")
    String content;

    String filePath;

    @Column(columnDefinition = "text")
    String metaData;

    String sagaId;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    ChatEventStatus status;


}
