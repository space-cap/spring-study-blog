package com.develead.smile.repository;

import com.develead.smile.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    private Customer customer;
    private Doctor doctor;
    private Clinic clinic;
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        // Role 생성
        Role role = new Role();
        role.setRoleName("USER");
        role = entityManager.persistAndFlush(role);

        // UserAccount 생성
        userAccount = new UserAccount();
        userAccount.setLoginId("test@example.com");
        userAccount.setPasswordHash("$2a$10$hashedPassword");
        userAccount.setRole(role);
        userAccount = entityManager.persistAndFlush(userAccount);

        // Customer 생성
        customer = new Customer();
        customer.setName("홍길동");
        customer.setPhoneNumber("010-1234-5678");
        customer.setUserAccount(userAccount);
        customer = entityManager.persistAndFlush(customer);

        // Clinic 생성
        clinic = new Clinic();
        clinic.setClinicName("스마일 치과");
        clinic.setAddress("서울시 강남구");
        clinic.setPhoneNumber("02-1234-5678");
        clinic = entityManager.persistAndFlush(clinic);

        // Doctor 생성
        doctor = new Doctor();
        doctor.setName("김의사");
        doctor.setSpecialty("일반치과");
        doctor.setClinic(clinic);
        doctor = entityManager.persistAndFlush(doctor);

        entityManager.clear();
    }

    @Test
    @DisplayName("예약 저장 - 성공")
    void saveAppointment_Success() {
        // Given
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDatetime(LocalDateTime.of(2024, 12, 25, 14, 0));
        appointment.setDescription("정기 검진");
        appointment.setStatus("예약중");

        // When
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Then
        assertNotNull(savedAppointment.getAppointment_id());
        assertEquals("정기 검진", savedAppointment.getDescription());
        assertEquals("예약중", savedAppointment.getStatus());
        assertEquals(customer.getCustomer_id(), savedAppointment.getCustomer().getCustomer_id());
        assertEquals(doctor.getDoctor_id(), savedAppointment.getDoctor().getDoctor_id());
    }

    @Test
    @DisplayName("고객 ID로 예약 조회 - 성공")
    void findAppointmentsByCustomerId_Success() {
        // Given
        Appointment appointment1 = createAppointment(LocalDateTime.of(2024, 12, 25, 14, 0), "정기 검진");
        Appointment appointment2 = createAppointment(LocalDateTime.of(2024, 12, 26, 10, 0), "충치 치료");
        
        entityManager.persistAndFlush(appointment1);
        entityManager.persistAndFlush(appointment2);

        // When
        List<Appointment> appointments = appointmentRepository.findAppointmentsByCustomerId(customer.getCustomer_id());

        // Then
        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().anyMatch(a -> a.getDescription().equals("정기 검진")));
        assertTrue(appointments.stream().anyMatch(a -> a.getDescription().equals("충치 치료")));
    }

    @Test
    @DisplayName("존재하지 않는 고객 ID로 예약 조회")
    void findAppointmentsByCustomerId_EmptyResult() {
        // When
        List<Appointment> appointments = appointmentRepository.findAppointmentsByCustomerId(999);

        // Then
        assertTrue(appointments.isEmpty());
    }

    @Test
    @DisplayName("예약 ID로 조회 - 성공")
    void findById_Success() {
        // Given
        Appointment appointment = createAppointment(LocalDateTime.of(2024, 12, 25, 14, 0), "정기 검진");
        Appointment savedAppointment = entityManager.persistAndFlush(appointment);

        // When
        Optional<Appointment> foundAppointment = appointmentRepository.findById(savedAppointment.getAppointment_id());

        // Then
        assertTrue(foundAppointment.isPresent());
        assertEquals("정기 검진", foundAppointment.get().getDescription());
        assertEquals(customer.getCustomer_id(), foundAppointment.get().getCustomer().getCustomer_id());
    }

    @Test
    @DisplayName("존재하지 않는 예약 ID로 조회")
    void findById_NotFound() {
        // When
        Optional<Appointment> foundAppointment = appointmentRepository.findById(999);

        // Then
        assertFalse(foundAppointment.isPresent());
    }

    @Test
    @DisplayName("예약 상태 업데이트")
    void updateAppointmentStatus() {
        // Given
        Appointment appointment = createAppointment(LocalDateTime.of(2024, 12, 25, 14, 0), "정기 검진");
        Appointment savedAppointment = entityManager.persistAndFlush(appointment);

        // When
        savedAppointment.setStatus("예약취소");
        Appointment updatedAppointment = appointmentRepository.save(savedAppointment);

        // Then
        assertEquals("예약취소", updatedAppointment.getStatus());
        
        // DB에서 다시 조회하여 확인
        entityManager.clear();
        Optional<Appointment> reloadedAppointment = appointmentRepository.findById(savedAppointment.getAppointment_id());
        assertTrue(reloadedAppointment.isPresent());
        assertEquals("예약취소", reloadedAppointment.get().getStatus());
    }

    @Test
    @DisplayName("예약 삭제")
    void deleteAppointment() {
        // Given
        Appointment appointment = createAppointment(LocalDateTime.of(2024, 12, 25, 14, 0), "정기 검진");
        Appointment savedAppointment = entityManager.persistAndFlush(appointment);
        Integer appointmentId = savedAppointment.getAppointment_id();

        // When
        appointmentRepository.delete(savedAppointment);
        entityManager.flush();

        // Then
        Optional<Appointment> deletedAppointment = appointmentRepository.findById(appointmentId);
        assertFalse(deletedAppointment.isPresent());
    }

    @Test
    @DisplayName("특정 날짜 범위의 예약 조회")
    void findAppointmentsByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 25);
        LocalDate endDate = LocalDate.of(2024, 12, 27);
        
        Appointment appointment1 = createAppointment(startDate.atTime(14, 0), "검진1");
        Appointment appointment2 = createAppointment(startDate.plusDays(1).atTime(10, 0), "검진2");
        Appointment appointment3 = createAppointment(startDate.plusDays(3).atTime(16, 0), "검진3"); // 범위 밖
        
        entityManager.persistAndFlush(appointment1);
        entityManager.persistAndFlush(appointment2);
        entityManager.persistAndFlush(appointment3);

        // When
        List<Appointment> appointments = appointmentRepository.findByAppointmentDatetimeBetween(
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59)
        );

        // Then
        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().anyMatch(a -> a.getDescription().equals("검진1")));
        assertTrue(appointments.stream().anyMatch(a -> a.getDescription().equals("검진2")));
        assertFalse(appointments.stream().anyMatch(a -> a.getDescription().equals("검진3")));
    }

    @Test
    @DisplayName("의사별 예약 조회")
    void findAppointmentsByDoctor() {
        // Given
        Doctor anotherDoctor = new Doctor();
        anotherDoctor.setName("박의사");
        anotherDoctor.setSpecialty("교정치과");
        anotherDoctor.setClinic(clinic);
        anotherDoctor = entityManager.persistAndFlush(anotherDoctor);

        Appointment appointment1 = createAppointmentWithDoctor(doctor, "검진1");
        Appointment appointment2 = createAppointmentWithDoctor(doctor, "검진2");
        Appointment appointment3 = createAppointmentWithDoctor(anotherDoctor, "교정치료");
        
        entityManager.persistAndFlush(appointment1);
        entityManager.persistAndFlush(appointment2);
        entityManager.persistAndFlush(appointment3);

        // When
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctor(doctor);

        // Then
        assertEquals(2, doctorAppointments.size());
        assertTrue(doctorAppointments.stream().allMatch(a -> a.getDoctor().getDoctor_id().equals(doctor.getDoctor_id())));
    }

    private Appointment createAppointment(LocalDateTime dateTime, String description) {
        return createAppointmentWithDoctor(doctor, description, dateTime);
    }

    private Appointment createAppointmentWithDoctor(Doctor doctor, String description) {
        return createAppointmentWithDoctor(doctor, description, LocalDateTime.of(2024, 12, 25, 14, 0));
    }

    private Appointment createAppointmentWithDoctor(Doctor doctor, String description, LocalDateTime dateTime) {
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDatetime(dateTime);
        appointment.setDescription(description);
        appointment.setStatus("예약중");
        return appointment;
    }
}