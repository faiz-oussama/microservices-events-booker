package com.ticketing.bookingservice.client;

import com.ticketing.bookingservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @GetMapping("/api/auth/validate-user/{userId}")
    Boolean validateUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/auth/user/{userId}/name")
    String getUserName(@PathVariable("userId") Long userId);

    @GetMapping("/api/auth/validate-token")
    UserDTO validateToken(@RequestHeader("Authorization") String token);
}