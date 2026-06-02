package com.distributed_lovable_clone.account_service.dto.subscription;

public record PlanLimitsResponse(
        String planName,
        Integer maxTokenPerDay,
        Integer maxProjects,
        Boolean unlimitedAi
) {
}
