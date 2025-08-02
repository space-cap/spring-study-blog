package com.develead.smile.config;

import com.develead.smile.domain.Role;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.repository.RoleRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 기본 역할(Role) 생성
        initializeRoles();

        // 기본 관리자 계정 생성
        initializeAdminUser();
    }

    private void initializeRoles() {
        Arrays.asList("ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_CUSTOMER").forEach(roleName -> {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                roleRepository.save(new Role(roleName));
            }
        });
    }

    private void initializeAdminUser() {
        if (userAccountRepository.findByLoginId("admin").isEmpty()) {
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not found."));

            UserAccount admin = new UserAccount();
            admin.setLoginId("admin");
            admin.setPasswordHash(passwordEncoder.encode("password")); // 초기 비밀번호: password
            admin.setRole(adminRole);
            userAccountRepository.save(admin);
        }
    }
}
