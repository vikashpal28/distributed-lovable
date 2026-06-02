package com.distributed_lovable_clone.workspace_service.entity;



import com.distributed_lovable_clone.common_lib.type.PreviewStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {
    Long id;
    Project project;
    String namespace;

    String podName;
    String previewUrl;
    @Enumerated(value = EnumType.STRING)
    PreviewStatus status;
    Instant startedAt;
    Instant terminatedAt;

    Instant createdAt;

}
