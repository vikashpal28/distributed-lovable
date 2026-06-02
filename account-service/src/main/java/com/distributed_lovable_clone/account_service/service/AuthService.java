package com.distributed_lovable_clone.account_service.service;


import com.distributed_lovable_clone.account_service.dto.auth.AuthResponse;
import com.distributed_lovable_clone.account_service.dto.auth.LoginRequest;
import com.distributed_lovable_clone.account_service.dto.auth.SignUpRequest;

public interface AuthService {
    AuthResponse signup(SignUpRequest request);

    AuthResponse login(LoginRequest request);
}
