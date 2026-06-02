package com.distributed_lovable_clone.workspace_service.dto.member;



import com.distributed_lovable_clone.common_lib.type.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String name,
        String email,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
