package com.distributed_lovable_clone.intelligence_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id" , nullable = false)
    Long userId;

    @Column(nullable = false)
    LocalDate date;

    Integer tokenUsed;
}
