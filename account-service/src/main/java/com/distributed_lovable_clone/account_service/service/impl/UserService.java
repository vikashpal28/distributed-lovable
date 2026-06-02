package com.distributed_lovable_clone.account_service.service.impl;

import com.distributed_lovable_clone.account_service.dto.auth.UserProfileResponse;
import com.distributed_lovable_clone.account_service.entity.User;
import com.distributed_lovable_clone.account_service.repository.UserRepository;
import com.distributed_lovable_clone.common_lib.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new JwtUserPrincipal(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public UserProfileResponse getProfile(Long id) {
      User user =  userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserProfileResponse(user.getId() , user.getUsername() , user.getName());
    }
}
