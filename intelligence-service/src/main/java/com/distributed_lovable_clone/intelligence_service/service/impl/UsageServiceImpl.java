package com.distributed_lovable_clone.intelligence_service.service.impl;


import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.intelligence_service.client.ApplicationClient;
import com.distributed_lovable_clone.intelligence_service.entity.UsageLog;
import com.distributed_lovable_clone.intelligence_service.repository.UsageLogRepository;
import com.distributed_lovable_clone.intelligence_service.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final ApplicationClient applicationClient;
    @Override
    public void recordTokenUsage(Long userId , int actualToken) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId , today).
                orElseGet(() -> createNewUsageLog(userId , today));
        todayLog.setTokenUsed(todayLog.getTokenUsed() + actualToken);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
     Long userId = authUtil.getCurrentUserId();
        PlanDto plan = applicationClient.getCurrentSubscribedPlanByUser();
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId , today).
                orElseGet(() -> createNewUsageLog(userId , today));

        int currentUsage = todayLog.getTokenUsed();
        int limit = plan.maxTokenPerDay();

        if(plan.unlimitedAi()) return;

        if(currentUsage >= limit){
          throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS , "Daily limit have reached and upgrade your plan");
        }
    }

    private UsageLog createNewUsageLog(Long userId , LocalDate today){
        UsageLog usageLog = UsageLog.builder()
                .userId(userId)
                .date(today)
                .tokenUsed(0)
                .build();
        return usageLogRepository.save(usageLog);
    }


}
