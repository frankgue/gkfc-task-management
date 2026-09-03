package com.gkfcsolution.taskmanager.initializer;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.UserRole;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer un admin si inexistant
        if (!userRepository.existsByEmail("admin@taskmanager.com")) {
            User admin = User.builder()
                    .email("admin@taskmanager.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("TaskManager")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user created: admin@taskmanager.com / admin123");
        }

        // Créer un utilisateur simple si inexistant
        if (!userRepository.existsByEmail("user@taskmanager.com")) {
            User user = User.builder()
                    .email("user@taskmanager.com")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("User")
                    .lastName("TaskManager")
                    .role(UserRole.MEMBER)
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("✅ User created: user@taskmanager.com / user123");
        }

        // Créer un project manager si inexistant
        if (!userRepository.existsByEmail("manager@taskmanager.com")) {
            User manager = User.builder()
                    .email("manager@taskmanager.com")
                    .password(passwordEncoder.encode("manager123"))
                    .firstName("Manager")
                    .lastName("TaskManager")
                    .role(UserRole.PROJECT_MANAGER)
                    .enabled(true)
                    .build();
            userRepository.save(manager);
            log.info("✅ Manager created: manager@taskmanager.com / manager123");
        }
    }
}