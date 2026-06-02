package com.distributed_lovable_clone.workspace_service.dto.project;



import com.distributed_lovable_clone.common_lib.type.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole role
) {
}
