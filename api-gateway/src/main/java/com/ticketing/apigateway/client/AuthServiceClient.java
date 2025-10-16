package com.ticketing.apigateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @GetMapping("/api/auth/validate-token")
    UserDTO validateToken(@RequestHeader("Authorization") String token);

    record UserDTO(
        Long id,
        String username,
        String email,
        String fullName,
        String role
    ) {}
}