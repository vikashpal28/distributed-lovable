package com.distributed_lovable_clone.intelligence_service.client;


import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "account-service" , path = "/account" , url = "${ACCOUNT_SERVICE_URL}")
public interface ApplicationClient {


    @GetMapping("/internal/v1/users/by-email")
    Optional<UserDto> getUserByEmail(@RequestParam String email);

    @GetMapping("/internal/v1/billing/current-plan")
    PlanDto getCurrentSubscribedPlanByUser();


}
