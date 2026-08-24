package com.example.taskmanager.config;

import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin123!"))
                .email("admin@taskmanager.com")
                .fullName("System Administrator")
                .role(Role.ADMIN)
                .active(true)
                .build();

        userRepository.save(admin);

        log.info("Seeded default admin: admin / Admin123!");
    }
}