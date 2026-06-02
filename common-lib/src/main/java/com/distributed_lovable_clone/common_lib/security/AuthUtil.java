package com.distributed_lovable_clone.common_lib.security;

import com.distributed_lovable_clone.common_lib.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(JwtUserPrincipal user) {
        return Jwts.builder()
                .subject(user.username())
                .claim("userId", user.userId().toString())
                .claim("name", user.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSecretKey())
                .compact();
    }

    public JwtUserPrincipal verifyAccessToken(String token) {
        try {
            // Changed log.error to log.info - it's not an error to verify a token!
            log.info("Verifying token: {}", token);
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.parseLong(claims.get("userId", String.class));
            String name = claims.get("name", String.class);
            String username = claims.getSubject();
            return new JwtUserPrincipal(userId,  name, username,null, new ArrayList<>());
        } catch (Exception e) {
            log.error("Token parsing failed: {}", e.getMessage());
            return null;
        }
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists and the principal is our expected type
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new AuthenticationCredentialsNotFoundException("No JWT Found in Security Context");
        }

        // Get the ID directly from the Principal that we set in JwtAuthFilter
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        return principal.getUserId();
    }
}