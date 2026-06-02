package com.distributed_lovable_clone.common_lib.dto;

public record PlanDto(
        Long id,
        String name,
        Integer maxProducts,
        Integer maxTokenPerDay,
        Boolean unlimitedAi,
        String price
) {
}
