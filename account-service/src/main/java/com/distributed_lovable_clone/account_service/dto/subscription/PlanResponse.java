package com.distributed_lovable_clone.account_service.dto.subscription;

public record PlanResponse(
         Long id,
         String name,
         Integer maxProducts,
         Integer maxTokenPerDay,
        Boolean unlimitedAi,
        String price
) {
}
