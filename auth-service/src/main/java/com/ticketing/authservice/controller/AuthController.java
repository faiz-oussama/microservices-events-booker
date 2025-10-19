package com.ticketing.authservice.controller;


import com.ticketing.authservice.DTO.LoginRequest;
import com.ticketing.authservice.DTO.LoginResponse;
import com.ticketing.authservice.DTO.RegisterRequest;
import com.ticketing.authservice.DTO.UserDTO;
import com.ticketing.authservice.service.AuthService;
import com.ticketing.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/validate-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long userId) {
        boolean exists = userService.validateUserExists(userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/{userId}/name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getUserName(@PathVariable Long userId) {
        String userName = userService.getUserFullName(userId);
        return ResponseEntity.ok(userName);
    }

    @GetMapping("/validate-token")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> validateToken(@RequestHeader("Authorization") String token) {
        try {
            UserDTO user = authService.validateToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAllUsers() {
        // This is a placeholder - in a real implementation, you'd return a list of users
        return ResponseEntity.ok("Admin endpoint: List of all users");
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getSystemStats() {
        // This is a placeholder - in a real implementation, you'd return system statistics
        return ResponseEntity.ok("Admin endpoint: System statistics");
    }
}
