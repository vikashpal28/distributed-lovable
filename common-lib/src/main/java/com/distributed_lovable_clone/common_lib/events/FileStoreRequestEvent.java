package com.distributed_lovable_clone.common_lib.events;

public record FileStoreRequestEvent(
        Long projectId,
        String sagaId,
        String filePath,
        String content,
        Long userId
) {

}
