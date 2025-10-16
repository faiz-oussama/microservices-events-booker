package com.ticketing.authservice.service;


import com.ticketing.authservice.DTO.LoginRequest;
import com.ticketing.authservice.DTO.LoginResponse;
import com.ticketing.authservice.DTO.RegisterRequest;
import com.ticketing.authservice.model.Role;
import com.ticketing.authservice.model.User;
import com.ticketing.authservice.model.enums.RoleType;
import com.ticketing.authservice.repository.RoleRepository;
import com.ticketing.authservice.repository.UserRepository;
import com.ticketing.authservice.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, RoleRepository roleRepository1, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository1;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // Authenticate using AuthenticationManager
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Generate JWT
        String token = jwtUtil.generateToken(String.valueOf(authentication));
        return new LoginResponse(token);
    }

    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername((registerRequest.getUsername()))) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role defaultRole = roleRepository.findByRoleType(RoleType.USER);
        user.setRole(defaultRole);

        userRepository.save(user);
    }

    public com.ticketing.authservice.DTO.UserDTO validateToken(String token) {
        try {
            // Extract username from token
            String username = jwtUtil.getUsernameFromToken(token);

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            // Get user from database
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Convert to DTO
            com.ticketing.authservice.DTO.UserDTO userDTO = new com.ticketing.authservice.DTO.UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFullName(user.getFullName());
            userDTO.setRole(user.getRole().getRoleType().toString());

            return userDTO;

        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }

}
