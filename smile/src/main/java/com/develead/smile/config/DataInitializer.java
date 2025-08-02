package com.develead.smile.config;

import com.develead.smile.domain.Clinic;
import com.develead.smile.domain.Doctor;
import com.develead.smile.domain.Role;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.repository.ClinicRepository;
import com.develead.smile.repository.DoctorRepository;
import com.develead.smile.repository.RoleRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
        initializeClinicAndDoctors();
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
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole(adminRole);
            userAccountRepository.save(admin);
        }
    }

    private void initializeClinicAndDoctors() {
        Clinic mainClinic = clinicRepository.findByClinicName("강남 본점").orElseGet(() -> {
            Clinic clinic = new Clinic();
            clinic.setClinicName("강남 본점");
            clinic.setAddress("서울시 강남구 테헤란로");
            return clinicRepository.save(clinic);
        });

        if (doctorRepository.findByName("김민국").isEmpty()) {
            Doctor doctorKim = new Doctor();
            doctorKim.setName("김민국");
            doctorKim.setSpecialty("임플란트 / 보철");
            doctorKim.setClinic(mainClinic);
            doctorRepository.save(doctorKim);
        }

        if (doctorRepository.findByName("박하나").isEmpty()) {
            Doctor doctorPark = new Doctor();
            doctorPark.setName("박하나");
            doctorPark.setSpecialty("치아교정 / 소아치과");
            doctorPark.setClinic(mainClinic);
            doctorRepository.save(doctorPark);
        }
    }
}
