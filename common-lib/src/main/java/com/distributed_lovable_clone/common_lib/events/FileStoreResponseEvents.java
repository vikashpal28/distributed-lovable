package com.distributed_lovable_clone.common_lib.events;

import lombok.Builder;

@Builder
public record FileStoreResponseEvents(
        String sagaId,
        boolean success,
        String errorMessage,
        Long projectId
) {
}
