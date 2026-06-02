package com.distributed_lovable_clone.api_gateway.filter;

import com.distributed_lovable_clone.api_gateway.config.SecurityProperty;
import com.distributed_lovable_clone.api_gateway.error.ApiError;
import com.distributed_lovable_clone.api_gateway.service.JwtGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayJwtAuthFilter implements GlobalFilter , Ordered {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final SecurityProperty securityProperty;
    private final JwtGatewayService jwtGatewayService;
    private final ObjectMapper objectMapper =  new ObjectMapper()
            .registerModule(new JavaTimeModule());


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean isPublic = securityProperty.publicRoutes.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));

        if (isPublic) {
            log.info("Public routes found {}" , path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Authorization header not found");
            return sendErrorResponse(exchange , HttpStatus.UNAUTHORIZED , "Authorization header not found");
        }

        String token = authHeader.substring(7);
        try{
            jwtGatewayService.validateToken(token);
            log.info("Token validated path {}", path);
        }
        catch (Exception ex){
        log.error("Token validation failed {}" , ex.getMessage());
        return sendErrorResponse(exchange , HttpStatus.UNAUTHORIZED, ex.getMessage());
        }

        return chain.filter(exchange);

    }

    public Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
     exchange.getResponse().setStatusCode(status);
     exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        ApiError apiError = new ApiError(status , message);

        try{
            byte[] bytes = objectMapper.writeValueAsBytes(apiError);
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } catch (Exception e) {
            log.error("Error sending error response {}" , e.getMessage());
            return  exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
