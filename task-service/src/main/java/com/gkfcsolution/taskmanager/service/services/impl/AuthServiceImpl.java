// task-service/src/main/java/com/gkfcsolution/taskmanager/service/services/impl/AuthServiceImpl.java
package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.UserRole;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import com.gkfcsolution.taskmanager.service.dto.request.AuthRequest;
import com.gkfcsolution.taskmanager.service.dto.request.RegisterRequest;
import com.gkfcsolution.taskmanager.service.dto.response.AuthResponse;
import com.gkfcsolution.taskmanager.service.dto.response.UserSummaryResponse;
import com.gkfcsolution.taskmanager.service.exception.BusinessException;
import com.gkfcsolution.taskmanager.service.exception.ResourceNotFoundException;
import com.gkfcsolution.taskmanager.service.exception.UnauthorizedException;
import com.gkfcsolution.taskmanager.service.mapper.UserMapper;
import com.gkfcsolution.taskmanager.service.security.JwtService;
import com.gkfcsolution.taskmanager.service.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        // Créer l'utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole() != null ? request.getRole() : UserRole.MEMBER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        // Générer les tokens
        return generateAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Authenticating user: {}", request.getEmail());

        try {
            // 1. Authentifier avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. ✅ CORRECTION : Récupérer l'utilisateur depuis la base de données
            // Ne pas caster authentication.getPrincipal() en User
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            if (!user.isEnabled()) {
                throw new BusinessException("Account is disabled");
            }

            log.info("User authenticated successfully: {}", user.getEmail());
            return generateAuthResponse(user);

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        log.info("Token refreshed for user: {}", userEmail);
        return generateAuthResponse(user);
    }

    @Override
    public void logout(String token) {
        log.info("Logging out user");
        jwtService.blacklistToken(token);
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Génère la réponse d'authentification
     */
    private AuthResponse generateAuthResponse(User user) {
        // Convertir User en UserDetails pour JwtService
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );

        // Générer les tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        UserSummaryResponse userSummary = userMapper.toSummary(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .user(userSummary)
                .build();
    }
}