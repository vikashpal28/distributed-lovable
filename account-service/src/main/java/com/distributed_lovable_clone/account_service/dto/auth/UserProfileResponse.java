package com.distributed_lovable_clone.account_service.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileResponse(
        Long id ,
        @Email @NotBlank
        String username,
        @NotBlank
        String name
        ) {
}
