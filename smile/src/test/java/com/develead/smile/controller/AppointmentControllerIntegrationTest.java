package com.develead.smile.controller;

import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserAccount userAccount;
    private Customer customer;
    private Doctor doctor;
    private Clinic clinic;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName("USER");
                    return roleRepository.save(newRole);
                });

        userAccount = new UserAccount();
        userAccount.setLoginId("test@example.com");
        userAccount.setPasswordHash("$2a$10$hashedPassword");
        userAccount.setRole(role);
        userAccount = userAccountRepository.save(userAccount);

        customer = new Customer();
        customer.setName("홍길동");
        customer.setPhoneNumber("010-1234-5678");
        customer.setUserAccount(userAccount);
        customer = customerRepository.save(customer);

        clinic = new Clinic();
        clinic.setClinicName("스마일 치과");
        clinic.setAddress("서울시 강남구");
        clinic.setPhoneNumber("02-1234-5678");
        clinic = clinicRepository.save(clinic);

        doctor = new Doctor();
        doctor.setName("김의사");
        doctor.setSpecialty("일반치과");
        doctor.setClinic(clinic);
        doctor = doctorRepository.save(doctor);
    }

    @Test
    @DisplayName("예약 페이지 조회 - 성공")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void showAppointmentPage_Success() throws Exception {
        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("appointmentDto"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    @DisplayName("예약 생성 - 성공")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void createAppointment_Success() throws Exception {
        mockMvc.perform(post("/appointments")
                        .with(csrf())
                        .param("doctorId", doctor.getDoctor_id().toString())
                        .param("appointmentDate", LocalDate.now().plusDays(1).toString())
                        .param("appointmentTime", "14:00")
                        .param("description", "정기 검진"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments"))
                .andExpect(flash().attribute("successMessage", "예약이 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("예약 생성 - 유효성 검증 실패")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void createAppointment_ValidationError() throws Exception {
        mockMvc.perform(post("/appointments")
                        .with(csrf())
                        .param("doctorId", "")
                        .param("appointmentDate", "")
                        .param("appointmentTime", "")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    @DisplayName("예약 생성 - 과거 날짜 검증")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void createAppointment_PastDateValidation() throws Exception {
        mockMvc.perform(post("/appointments")
                        .with(csrf())
                        .param("doctorId", doctor.getDoctor_id().toString())
                        .param("appointmentDate", LocalDate.now().minusDays(1).toString())
                        .param("appointmentTime", "14:00")
                        .param("description", "정기 검진"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("예약 취소 - 성공")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void cancelAppointment_Success() throws Exception {
        // Given: 예약 생성
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDatetime(LocalDate.now().plusDays(1).atTime(14, 0));
        appointment.setDescription("정기 검진");
        appointment.setStatus("예약중");
        appointment = appointmentRepository.save(appointment);

        // When & Then
        mockMvc.perform(post("/appointments/cancel/" + appointment.getAppointment_id())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments"))
                .andExpect(flash().attribute("successMessage", "예약이 취소되었습니다."));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 차단")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/appointments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("다른 사용자의 예약 취소 시도")
    @WithMockUser(username = "other@example.com", roles = {"USER"})
    void cancelOtherUserAppointment() throws Exception {
        // Given: 다른 사용자 계정 생성
        Role role = roleRepository.findByRoleName("USER").orElse(null);
        UserAccount otherUser = new UserAccount();
        otherUser.setLoginId("other@example.com");
        otherUser.setPasswordHash("$2a$10$hashedPassword");
        otherUser.setRole(role);
        otherUser = userAccountRepository.save(otherUser);

        Customer otherCustomer = new Customer();
        otherCustomer.setName("김철수");
        otherCustomer.setPhoneNumber("010-9876-5432");
        otherCustomer.setUserAccount(otherUser);
        otherCustomer = customerRepository.save(otherCustomer);

        // 원래 사용자의 예약 생성
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDatetime(LocalDate.now().plusDays(1).atTime(14, 0));
        appointment.setDescription("정기 검진");
        appointment.setStatus("예약중");
        appointment = appointmentRepository.save(appointment);

        // When & Then: 다른 사용자로 예약 취소 시도
        mockMvc.perform(post("/appointments/cancel/" + appointment.getAppointment_id())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments"))
                .andExpect(flash().attribute("errorMessage", "예약 취소 중 오류가 발생했습니다."));
    }
}