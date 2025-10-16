package com.ticketing.authservice.service;

import com.ticketing.authservice.model.User;
import com.ticketing.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean validateUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public String getUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getFullName() != null ? user.getFullName() : "Unknown User")
                .orElse("User not found");
    }
}
