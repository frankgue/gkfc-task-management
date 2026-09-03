package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.service.dto.request.AuthRequest;
import com.gkfcsolution.taskmanager.service.dto.request.RegisterRequest;
import com.gkfcsolution.taskmanager.service.dto.response.AuthResponse;
import org.springframework.security.core.Authentication;

/**
 * Created on 2026 at 09:58
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:58
 */
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
    User getCurrentUser(Authentication authentication);
}
