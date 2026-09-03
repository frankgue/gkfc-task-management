package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import com.gkfcsolution.taskmanager.service.dto.request.UserUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.UserSummaryResponse;
import com.gkfcsolution.taskmanager.service.exception.ResourceNotFoundException;
import com.gkfcsolution.taskmanager.service.services.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Created on 2026 at 11:24
 * File: UserServiceImpl.java.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 11:24
 */
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserSummaryResponse getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        return mapToSummary(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserSummaryResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return mapToSummary(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserSummaryResponse> getAllUsers() {
        log.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    @Override
    public UserSummaryResponse updateUser(UUID id, UserUpdateRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());

        return mapToSummary(updatedUser);
    }

    @Override
    public void changePassword(UUID id, String currentPassword, String newPassword) {
        log.info("Changing password for user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", id);
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    // Util method
    private UserSummaryResponse mapToSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .fullName(user.getFullName())
                .build();
    }
}
