package com.distributed_lovable_clone.account_service.contoller;

import com.distributed_lovable_clone.account_service.mapper.UserMapper;
import com.distributed_lovable_clone.account_service.repository.UserRepository;
import com.distributed_lovable_clone.account_service.service.SubscriptionService;
import com.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.distributed_lovable_clone.common_lib.dto.UserDto;
import com.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/internal/v1")
@RequiredArgsConstructor
public class InternalApiController {
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new ResourceNotFoundException("User" , id.toString()));
    }

    @GetMapping("/users/by-email")
    public Optional<UserDto> getUserByEmail(@RequestParam String email) {
        return userRepository.findByUsernameIgnoreCase(email)
                .map(userMapper::toUserDto);
    }

    @GetMapping("/billing/current-plan")
    public PlanDto getCurrentSubscriptionPlan() {
        return subscriptionService.getCurrentSubscribedPlanByUser();
    }
}
