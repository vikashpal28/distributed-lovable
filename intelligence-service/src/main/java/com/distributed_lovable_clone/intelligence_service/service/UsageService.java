package com.distributed_lovable_clone.intelligence_service.service;

public interface UsageService {
    void recordTokenUsage(Long userId , int actualToken);

    public void checkDailyTokensUsage();
}
