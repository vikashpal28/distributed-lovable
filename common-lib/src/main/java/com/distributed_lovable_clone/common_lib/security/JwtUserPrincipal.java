package com.distributed_lovable_clone.common_lib.security;


import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



public record JwtUserPrincipal(
        Long userId,
        String name,
        String username,
        String password,
        List<GrantedAuthority> authorities
) implements UserDetails {
    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username();
    }
}
