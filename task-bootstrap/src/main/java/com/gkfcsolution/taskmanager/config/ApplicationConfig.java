// task-bootstrap/src/main/java/com/gkfcsolution/taskmanager/config/ApplicationConfig.java
package com.gkfcsolution.taskmanager.config;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("🔍 Loading user by username: {}", username);

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.error("❌ User not found: {}", username);
                        return new UsernameNotFoundException("User not found: " + username);
                    });

            // ✅ Convertir User en UserDetails
            return new org.springframework.security.core.userdetails.User(
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
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("🔐 Creating AuthenticationProvider...");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        log.info("✅ AuthenticationProvider created");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("🔐 Creating AuthenticationManager...");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}