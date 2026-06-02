package com.distributed_lovable_clone.workspace_service.dto.member;


import com.distributed_lovable_clone.common_lib.type.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitedMemberRequest(
        @Email
        @NotBlank
        String username ,
        @NotNull
        ProjectRole role
) {
}
