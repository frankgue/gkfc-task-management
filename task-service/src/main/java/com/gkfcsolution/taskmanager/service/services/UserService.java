package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.service.dto.request.UserUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.UserSummaryResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

/**
 * Created on 2026 at 09:58
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:58
 */
public interface UserService {
    UserSummaryResponse getUserById(UUID id);
    UserSummaryResponse getUserByEmail(String email);
    List<UserSummaryResponse> getAllUsers();
    UserSummaryResponse updateUser(UUID id, UserUpdateRequest request);
    void changePassword(UUID id, String currentPassword, String newPassword);
    void deleteUser(UUID id);
    User getCurrentUser(Authentication authentication);
}
