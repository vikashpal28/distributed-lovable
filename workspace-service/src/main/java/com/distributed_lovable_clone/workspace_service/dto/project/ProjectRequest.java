package com.distributed_lovable_clone.workspace_service.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ProjectRequest(
        @NotBlank
        @Size(min = 1 , max = 30)
        String name
) {
}
