package com.distributed_lovable_clone.account_service.service.impl;


import com.distributed_lovable_clone.account_service.dto.auth.AuthResponse;
import com.distributed_lovable_clone.account_service.dto.auth.LoginRequest;
import com.distributed_lovable_clone.account_service.dto.auth.SignUpRequest;
import com.distributed_lovable_clone.account_service.entity.User;
import com.distributed_lovable_clone.account_service.mapper.UserMapper;
import com.distributed_lovable_clone.account_service.repository.UserRepository;
import com.distributed_lovable_clone.account_service.service.AuthService;
import com.distributed_lovable_clone.common_lib.error.BadRequestException;
import com.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.distributed_lovable_clone.common_lib.security.JwtUserPrincipal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;
    @Override
    public AuthResponse signup(SignUpRequest request) {
  userRepository.findByUsername(request.username()).ifPresent(
          user -> {
              throw new BadRequestException("userId already exists " + request.username());
          }
  );
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
      user =   userRepository.save(user);
        JwtUserPrincipal jwtUserPrincipal = new JwtUserPrincipal(
                user.getId() , user.getName() , user.getUsername() , null , new ArrayList<>()
        );

      String AccessToken = authUtil.generateAccessToken(jwtUserPrincipal);



        return new AuthResponse(AccessToken , userMapper.toUserProfileResponse(jwtUserPrincipal));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username() , request.password())
        );
        JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();

        String AccessToken = authUtil.generateAccessToken(user);
        return new AuthResponse(AccessToken , userMapper.toUserProfileResponse(user));
    }
}
