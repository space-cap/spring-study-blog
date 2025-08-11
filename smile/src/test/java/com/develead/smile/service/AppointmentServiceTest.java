package com.develead.smile.service;

import com.develead.smile.domain.*;
import com.develead.smile.dto.AppointmentDto;
import com.develead.smile.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private AppointmentChangeLogRepository appointmentChangeLogRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    private UserAccount userAccount;
    private Customer customer;
    private Doctor doctor;
    private Clinic clinic;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole_id(1);
        role.setRoleName("USER");

        userAccount = new UserAccount();
        userAccount.setUser_account_id(1);
        userAccount.setLoginId("test@example.com");
        userAccount.setRole(role);

        customer = new Customer();
        customer.setCustomer_id(1);
        customer.setName("홍길동");
        customer.setUserAccount(userAccount);

        clinic = new Clinic();
        clinic.setClinic_id(1);
        clinic.setClinicName("스마일 치과");

        doctor = new Doctor();
        doctor.setDoctor_id(1);
        doctor.setName("김의사");
        doctor.setClinic(clinic);
    }

    @Test
    @DisplayName("예약 생성 - 성공")
    void createAppointment_Success() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(1);
        dto.setAppointmentDate(LocalDate.of(2024, 12, 25));
        dto.setAppointmentTime(LocalTime.of(14, 0));
        dto.setDescription("정기 검진");

        Appointment savedAppointment = new Appointment();
        savedAppointment.setAppointment_id(1);
        savedAppointment.setCustomer(customer);
        savedAppointment.setDoctor(doctor);
        savedAppointment.setClinic(clinic);
        savedAppointment.setAppointmentDatetime(LocalDateTime.of(2024, 12, 25, 14, 0));
        savedAppointment.setStatus("예약중");

        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));
        when(customerRepository.findByUserAccount(userAccount)).thenReturn(Optional.of(customer));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        assertDoesNotThrow(() -> appointmentService.createAppointment(dto, "test@example.com"));

        // Then
        verify(appointmentRepository).save(any(Appointment.class));
        verify(appointmentChangeLogRepository).save(any(AppointmentChangeLog.class));
        verify(notificationService).sendNotification(eq(customer), eq("APPOINTMENT_CONFIRMATION"), any());
    }

    @Test
    @DisplayName("예약 생성 - 사용자 없음 예외")
    void createAppointment_UserNotFound() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(1);
        
        when(userAccountRepository.findByLoginId("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> appointmentService.createAppointment(dto, "nonexistent@example.com"));
    }

    @Test
    @DisplayName("예약 생성 - 의사 없음 예외")
    void createAppointment_DoctorNotFound() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(999);
        
        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));
        when(customerRepository.findByUserAccount(userAccount)).thenReturn(Optional.of(customer));
        when(doctorRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> appointmentService.createAppointment(dto, "test@example.com"));
    }

    @Test
    @DisplayName("사용자 예약 조회 - 성공")
    void findAppointmentsByLoginId_Success() {
        // Given
        Appointment appointment1 = new Appointment();
        appointment1.setAppointment_id(1);
        appointment1.setCustomer(customer);
        appointment1.setDoctor(doctor);

        Appointment appointment2 = new Appointment();
        appointment2.setAppointment_id(2);
        appointment2.setCustomer(customer);
        appointment2.setDoctor(doctor);

        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));
        when(customerRepository.findByUserAccount(userAccount)).thenReturn(Optional.of(customer));
        when(appointmentRepository.findAppointmentsByCustomerId(1)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.findAppointmentsByLoginId("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAppointments, result);
    }

    @Test
    @DisplayName("예약 취소 - 성공")
    void cancelAppointment_Success() {
        // Given
        Appointment appointment = new Appointment();
        appointment.setAppointment_id(1);
        appointment.setCustomer(customer);
        appointment.setStatus("예약중");

        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));
        when(customerRepository.findByUserAccount(userAccount)).thenReturn(Optional.of(customer));

        // When
        assertDoesNotThrow(() -> appointmentService.cancelAppointment(1, "test@example.com"));

        // Then
        assertEquals("예약취소", appointment.getStatus());
        verify(appointmentRepository).save(appointment);
        verify(appointmentChangeLogRepository).save(any(AppointmentChangeLog.class));
    }

    @Test
    @DisplayName("예약 취소 - 권한 없음 예외")
    void cancelAppointment_Unauthorized() {
        // Given
        Customer otherCustomer = new Customer();
        otherCustomer.setCustomer_id(2);
        otherCustomer.setName("다른 고객");

        Appointment appointment = new Appointment();
        appointment.setAppointment_id(1);
        appointment.setCustomer(otherCustomer);

        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));
        when(customerRepository.findByUserAccount(userAccount)).thenReturn(Optional.of(customer));

        // When & Then
        assertThrows(SecurityException.class, 
            () -> appointmentService.cancelAppointment(1, "test@example.com"));
    }

    @Test
    @DisplayName("예약 취소 - 존재하지 않는 예약")
    void cancelAppointment_AppointmentNotFound() {
        // Given
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> appointmentService.cancelAppointment(999, "test@example.com"));
    }
}