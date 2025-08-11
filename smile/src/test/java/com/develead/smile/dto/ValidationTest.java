package com.develead.smile.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AppointmentDto 유효성 검증 - 성공")
    void appointmentDto_ValidData() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(1);
        dto.setAppointmentDate(LocalDate.now().plusDays(1));
        dto.setAppointmentTime(LocalTime.of(14, 0));
        dto.setDescription("정기 검진");

        // When
        Set<ConstraintViolation<AppointmentDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("AppointmentDto 유효성 검증 - 의사 ID 누락")
    void appointmentDto_MissingDoctorId() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setAppointmentDate(LocalDate.now().plusDays(1));
        dto.setAppointmentTime(LocalTime.of(14, 0));
        dto.setDescription("정기 검진");

        // When
        Set<ConstraintViolation<AppointmentDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("의사를 선택해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("AppointmentDto 유효성 검증 - 과거 날짜")
    void appointmentDto_PastDate() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(1);
        dto.setAppointmentDate(LocalDate.now().minusDays(1));
        dto.setAppointmentTime(LocalTime.of(14, 0));
        dto.setDescription("정기 검진");

        // When
        Set<ConstraintViolation<AppointmentDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("오늘 이후의 날짜를 선택해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("AppointmentDto 유효성 검증 - 설명 누락")
    void appointmentDto_EmptyDescription() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(1);
        dto.setAppointmentDate(LocalDate.now().plusDays(1));
        dto.setAppointmentTime(LocalTime.of(14, 0));
        dto.setDescription("");

        // When
        Set<ConstraintViolation<AppointmentDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("증상을 입력해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("AppointmentDto 유효성 검증 - 여러 필드 오류")
    void appointmentDto_MultipleViolations() {
        // Given
        AppointmentDto dto = new AppointmentDto();
        dto.setAppointmentDate(LocalDate.now().minusDays(1));
        dto.setDescription("");

        // When
        Set<ConstraintViolation<AppointmentDto>> violations = validator.validate(dto);

        // Then
        assertEquals(4, violations.size()); // doctorId, appointmentDate, appointmentTime, description
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 성공")
    void userRegistrationDto_ValidData() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLoginId("testuser123");
        dto.setPassword("password123");
        dto.setName("홍길동");
        dto.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 로그인 ID 너무 짧음")
    void userRegistrationDto_LoginIdTooShort() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLoginId("abc");
        dto.setPassword("password123");
        dto.setName("홍길동");
        dto.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("아이디는 4자 이상 20자 이하로 입력해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 로그인 ID 너무 김")
    void userRegistrationDto_LoginIdTooLong() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLoginId("verylongloginidthatisinvalid");
        dto.setPassword("password123");
        dto.setName("홍길동");
        dto.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("아이디는 4자 이상 20자 이하로 입력해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 비밀번호 너무 짧음")
    void userRegistrationDto_PasswordTooShort() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLoginId("testuser123");
        dto.setPassword("12345");
        dto.setName("홍길동");
        dto.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("비밀번호는 6자 이상으로 입력해주세요.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 모든 필수 필드 누락")
    void userRegistrationDto_AllFieldsEmpty() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLoginId("");
        dto.setPassword("");
        dto.setName("");
        dto.setPhoneNumber("");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertEquals(6, violations.size()); // 모든 필드 + 길이 검증
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - null 값")
    void userRegistrationDto_NullValues() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertEquals(4, violations.size()); // 모든 필드 null
    }

    @Test
    @DisplayName("UserRegistrationDto 유효성 검증 - 경계값 테스트")
    void userRegistrationDto_BoundaryValues() {
        // Given - 최소 길이
        UserRegistrationDto dto1 = new UserRegistrationDto();
        dto1.setLoginId("abcd"); // 4자 (최소)
        dto1.setPassword("123456"); // 6자 (최소)
        dto1.setName("김");
        dto1.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations1 = validator.validate(dto1);

        // Then
        assertTrue(violations1.isEmpty());

        // Given - 최대 길이
        UserRegistrationDto dto2 = new UserRegistrationDto();
        dto2.setLoginId("12345678901234567890"); // 20자 (최대)
        dto2.setPassword("password123456789");
        dto2.setName("홍길동");
        dto2.setPhoneNumber("010-1234-5678");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations2 = validator.validate(dto2);

        // Then
        assertTrue(violations2.isEmpty());
    }
}