package com.develead.smile.e2e;

import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppointmentE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserAccount testUser;
    private Customer testCustomer;
    private Doctor testDoctor;

    @BeforeAll
    static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;

        setupTestData();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Transactional
    void setupTestData() {
        // Role 설정
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("USER");
                    return roleRepository.save(role);
                });

        // 테스트 사용자 생성
        testUser = new UserAccount();
        testUser.setLoginId("testuser@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setRole(userRole);
        testUser = userAccountRepository.save(testUser);

        // 테스트 고객 생성
        testCustomer = new Customer();
        testCustomer.setName("테스트사용자");
        testCustomer.setPhoneNumber("010-1234-5678");
        testCustomer.setUserAccount(testUser);
        testCustomer = customerRepository.save(testCustomer);

        // 테스트 클리닉 생성
        Clinic testClinic = new Clinic();
        testClinic.setClinicName("스마일 치과");
        testClinic.setAddress("서울시 강남구");
        testClinic.setPhoneNumber("02-1234-5678");
        testClinic = clinicRepository.save(testClinic);

        // 테스트 의사 생성
        testDoctor = new Doctor();
        testDoctor.setName("김의사");
        testDoctor.setSpecialty("일반치과");
        testDoctor.setClinic(testClinic);
        testDoctor = doctorRepository.save(testDoctor);
    }

    @Test
    @Order(1)
    @DisplayName("E2E: 전체 예약 프로세스 - 로그인부터 예약 완료까지")
    void fullAppointmentProcess() {
        // 1. 홈페이지 접속
        driver.get(baseUrl);
        assertEquals("Smile Dental Clinic", driver.getTitle());

        // 2. 로그인 페이지로 이동
        WebElement loginLink = driver.findElement(By.linkText("로그인"));
        loginLink.click();

        // 3. 로그인 수행
        WebElement loginIdInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        loginIdInput.sendKeys("testuser@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // 4. 로그인 성공 확인 (대시보드 또는 홈페이지로 리다이렉션)
        assertTrue(driver.getCurrentUrl().contains(baseUrl));

        // 5. 예약 페이지로 이동
        driver.get(baseUrl + "/appointments");

        // 6. 예약 폼 작성
        Select doctorSelect = new Select(driver.findElement(By.name("doctorId")));
        doctorSelect.selectByVisibleText("김의사 (일반치과)");

        WebElement dateInput = driver.findElement(By.name("appointmentDate"));
        String futureDate = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateInput.clear();
        dateInput.sendKeys(futureDate);

        WebElement timeInput = driver.findElement(By.name("appointmentTime"));
        timeInput.clear();
        timeInput.sendKeys("14:00");

        WebElement descriptionInput = driver.findElement(By.name("description"));
        descriptionInput.clear();
        descriptionInput.sendKeys("정기 검진 예약입니다.");

        // 7. 예약 신청
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 8. 예약 완료 확인
        assertTrue(driver.getCurrentUrl().contains("/appointments"));
        
        // 성공 메시지 확인
        try {
            WebElement successMessage = driver.findElement(By.cssSelector(".alert-success, .success-message"));
            assertTrue(successMessage.getText().contains("예약이 성공적으로 완료되었습니다"));
        } catch (Exception e) {
            // 메시지가 없을 수 있으므로 예약 목록에서 확인
        }

        // 9. 예약 목록에서 새로운 예약 확인
        WebElement appointmentTable = driver.findElement(By.cssSelector("table, .appointment-list"));
        assertTrue(appointmentTable.getText().contains("정기 검진 예약입니다"));
    }

    @Test
    @Order(2)
    @DisplayName("E2E: 예약 취소 프로세스")
    void appointmentCancellationProcess() {
        // 선행 조건: 로그인
        loginAsTestUser();

        // 예약 페이지로 이동
        driver.get(baseUrl + "/appointments");

        // 기존 예약이 있는지 확인하고, 없다면 먼저 예약 생성
        if (!driver.getPageSource().contains("정기 검진")) {
            createTestAppointment();
        }

        // 예약 취소 버튼 클릭
        WebElement cancelButton = driver.findElement(By.cssSelector("form[action*='/cancel/'] button, .cancel-btn"));
        cancelButton.click();

        // 취소 확인 메시지
        assertTrue(driver.getCurrentUrl().contains("/appointments"));
        
        // 취소된 예약의 상태 확인
        try {
            WebElement cancelMessage = driver.findElement(By.cssSelector(".alert-success, .success-message"));
            assertTrue(cancelMessage.getText().contains("예약이 취소되었습니다"));
        } catch (Exception e) {
            // 상태 변경 확인
            assertTrue(driver.getPageSource().contains("예약취소"));
        }
    }

    @Test
    @Order(3)
    @DisplayName("E2E: 예약 폼 유효성 검증")
    void appointmentFormValidation() {
        // 로그인
        loginAsTestUser();

        // 예약 페이지로 이동
        driver.get(baseUrl + "/appointments");

        // 빈 폼으로 제출
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 유효성 검증 오류 메시지 확인
        assertTrue(driver.getCurrentUrl().contains("/appointments"));
        assertTrue(driver.getPageSource().contains("의사를 선택해주세요") || 
                  driver.getPageSource().contains("필수") ||
                  driver.findElements(By.cssSelector(".error, .invalid-feedback")).size() > 0);
    }

    @Test
    @Order(4)
    @DisplayName("E2E: 과거 날짜 예약 시도")
    void pastDateAppointmentValidation() {
        // 로그인
        loginAsTestUser();

        // 예약 페이지로 이동
        driver.get(baseUrl + "/appointments");

        // 의사 선택
        Select doctorSelect = new Select(driver.findElement(By.name("doctorId")));
        doctorSelect.selectByIndex(1); // 첫 번째 의사 선택

        // 과거 날짜 입력
        WebElement dateInput = driver.findElement(By.name("appointmentDate"));
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateInput.clear();
        dateInput.sendKeys(pastDate);

        WebElement timeInput = driver.findElement(By.name("appointmentTime"));
        timeInput.clear();
        timeInput.sendKeys("14:00");

        WebElement descriptionInput = driver.findElement(By.name("description"));
        descriptionInput.clear();
        descriptionInput.sendKeys("테스트 예약");

        // 제출
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 유효성 검증 오류 확인
        assertTrue(driver.getCurrentUrl().contains("/appointments"));
        assertTrue(driver.getPageSource().contains("오늘 이후의 날짜") || 
                  driver.findElements(By.cssSelector(".error, .invalid-feedback")).size() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("E2E: 인증되지 않은 사용자 접근 차단")
    void unauthorizedAccessTest() {
        // 로그아웃 상태에서 예약 페이지 접근
        driver.get(baseUrl + "/appointments");

        // 로그인 페이지로 리다이렉션되는지 확인
        assertTrue(driver.getCurrentUrl().contains("/login") || 
                  driver.getCurrentUrl().contains("/auth"));
    }

    private void loginAsTestUser() {
        driver.get(baseUrl + "/login");
        
        WebElement loginIdInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        loginIdInput.clear();
        loginIdInput.sendKeys("testuser@example.com");
        passwordInput.clear();
        passwordInput.sendKeys("password123");
        loginButton.click();

        // 로그인 완료 대기
        wait.until(webDriver -> !webDriver.getCurrentUrl().contains("/login"));
    }

    private void createTestAppointment() {
        Select doctorSelect = new Select(driver.findElement(By.name("doctorId")));
        doctorSelect.selectByIndex(1);

        WebElement dateInput = driver.findElement(By.name("appointmentDate"));
        String futureDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateInput.clear();
        dateInput.sendKeys(futureDate);

        WebElement timeInput = driver.findElement(By.name("appointmentTime"));
        timeInput.clear();
        timeInput.sendKeys("10:00");

        WebElement descriptionInput = driver.findElement(By.name("description"));
        descriptionInput.clear();
        descriptionInput.sendKeys("정기 검진");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 예약 완료 대기
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/appointments"));
    }
}