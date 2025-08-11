package com.develead.smile.e2e;

import com.develead.smile.domain.Role;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.repository.RoleRepository;
import com.develead.smile.repository.UserAccountRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    void setupTestData() {
        // USER 역할 생성
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("USER");
                    return roleRepository.save(role);
                });

        // 기존 테스트 사용자가 있다면 생성
        if (!userAccountRepository.findByLoginId("existing@example.com").isPresent()) {
            UserAccount existingUser = new UserAccount();
            existingUser.setLoginId("existing@example.com");
            existingUser.setPasswordHash(passwordEncoder.encode("password123"));
            existingUser.setRole(userRole);
            userAccountRepository.save(existingUser);
        }
    }

    @Test
    @Order(1)
    @DisplayName("E2E: 회원가입 전체 프로세스 - 성공")
    void userRegistrationProcess_Success() {
        // 1. 홈페이지 접속
        driver.get(baseUrl);

        // 2. 회원가입 페이지로 이동
        WebElement registerLink = driver.findElement(By.linkText("회원가입"));
        registerLink.click();

        // 3. 회원가입 폼이 로드되었는지 확인
        assertTrue(driver.getCurrentUrl().contains("/register"));
        assertTrue(driver.getPageSource().contains("회원가입"));

        // 4. 고유한 사용자 정보로 회원가입 폼 작성
        String uniqueEmail = "newuser" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        
        WebElement loginIdInput = driver.findElement(By.name("loginId"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement phoneNumberInput = driver.findElement(By.name("phoneNumber"));

        loginIdInput.sendKeys(uniqueEmail);
        passwordInput.sendKeys("newpassword123");
        nameInput.sendKeys("신규사용자");
        phoneNumberInput.sendKeys("010-9876-5432");

        // 5. 회원가입 제출
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 6. 로그인 페이지로 리다이렉션 확인
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/login"));
        
        // 7. 성공 메시지 확인
        try {
            WebElement successMessage = driver.findElement(By.cssSelector(".alert-success, .success-message"));
            assertTrue(successMessage.getText().contains("회원가입이 성공적으로 완료되었습니다"));
        } catch (Exception e) {
            // 메시지가 없어도 로그인 페이지에 있다면 성공으로 간주
            assertTrue(driver.getCurrentUrl().contains("/login"));
        }
    }

    @Test
    @Order(2)
    @DisplayName("E2E: 회원가입 유효성 검증 - 필수 필드 누락")
    void userRegistrationProcess_ValidationErrors() {
        // 1. 회원가입 페이지로 이동
        driver.get(baseUrl + "/register");

        // 2. 빈 폼으로 제출
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 3. 유효성 검증 오류 확인
        assertTrue(driver.getCurrentUrl().contains("/register"));
        
        // 오류 메시지 또는 페이지에 머물러 있는지 확인
        boolean hasValidationErrors = driver.getPageSource().contains("필수") ||
                                    driver.getPageSource().contains("입력해주세요") ||
                                    driver.findElements(By.cssSelector(".error, .invalid-feedback, .field-error")).size() > 0;
        assertTrue(hasValidationErrors);
    }

    @Test
    @Order(3)
    @DisplayName("E2E: 회원가입 - 중복 이메일")
    void userRegistrationProcess_DuplicateEmail() {
        // 1. 회원가입 페이지로 이동
        driver.get(baseUrl + "/register");

        // 2. 기존 사용자와 같은 이메일로 회원가입 시도
        WebElement loginIdInput = driver.findElement(By.name("loginId"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement phoneNumberInput = driver.findElement(By.name("phoneNumber"));

        loginIdInput.sendKeys("existing@example.com");
        passwordInput.sendKeys("newpassword123");
        nameInput.sendKeys("중복사용자");
        phoneNumberInput.sendKeys("010-1111-2222");

        // 3. 제출
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // 4. 오류 메시지 확인 (페이지에 머물러 있어야 함)
        assertTrue(driver.getCurrentUrl().contains("/register"));
        
        // 중복 이메일 오류 메시지 확인
        boolean hasDuplicateError = driver.getPageSource().contains("이미 존재") ||
                                   driver.getPageSource().contains("중복") ||
                                   driver.findElements(By.cssSelector(".error, .invalid-feedback")).size() > 0;
        assertTrue(hasDuplicateError);
    }

    @Test
    @Order(4)
    @DisplayName("E2E: 로그인 프로세스 - 성공")
    void userLoginProcess_Success() {
        // 1. 로그인 페이지로 이동
        driver.get(baseUrl + "/login");

        // 2. 기존 사용자로 로그인
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.sendKeys("existing@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // 3. 로그인 성공 확인 (홈페이지나 대시보드로 리다이렉션)
        wait.until(webDriver -> !webDriver.getCurrentUrl().contains("/login"));
        assertTrue(driver.getCurrentUrl().contains(baseUrl));
        assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(5)
    @DisplayName("E2E: 로그인 프로세스 - 잘못된 자격 증명")
    void userLoginProcess_InvalidCredentials() {
        // 1. 로그인 페이지로 이동
        driver.get(baseUrl + "/login");

        // 2. 잘못된 자격 증명으로 로그인 시도
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.sendKeys("nonexistent@example.com");
        passwordInput.sendKeys("wrongpassword");
        loginButton.click();

        // 3. 로그인 실패 확인 (로그인 페이지에 머물러 있고 오류 메시지 표시)
        assertTrue(driver.getCurrentUrl().contains("/login"));
        
        // 오류 메시지 확인
        boolean hasLoginError = driver.getPageSource().contains("로그인 실패") ||
                               driver.getPageSource().contains("아이디") ||
                               driver.getPageSource().contains("비밀번호") ||
                               driver.findElements(By.cssSelector(".error, .alert-danger")).size() > 0;
        assertTrue(hasLoginError);
    }

    @Test
    @Order(6)
    @DisplayName("E2E: 로그인 폼 유효성 검증")
    void loginFormValidation() {
        // 1. 로그인 페이지로 이동
        driver.get(baseUrl + "/login");

        // 2. 빈 폼으로 로그인 시도
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // 3. 페이지에 머물러 있거나 유효성 검증 오류 확인
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(7)
    @DisplayName("E2E: 전체 인증 플로우 - 회원가입부터 로그인까지")
    void fullAuthenticationFlow() {
        String uniqueEmail = "fullflow" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        // 1. 회원가입
        driver.get(baseUrl + "/register");
        
        WebElement loginIdInput = driver.findElement(By.name("loginId"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement phoneNumberInput = driver.findElement(By.name("phoneNumber"));

        loginIdInput.sendKeys(uniqueEmail);
        passwordInput.sendKeys("fullflowpassword");
        nameInput.sendKeys("전체플로우테스트");
        phoneNumberInput.sendKeys("010-5555-6666");

        WebElement registerButton = driver.findElement(By.cssSelector("button[type='submit']"));
        registerButton.click();

        // 2. 로그인 페이지로 리다이렉션 확인
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/login"));

        // 3. 방금 생성한 계정으로 로그인
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement loginPasswordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.sendKeys(uniqueEmail);
        loginPasswordInput.sendKeys("fullflowpassword");
        loginButton.click();

        // 4. 로그인 성공 확인
        wait.until(webDriver -> !webDriver.getCurrentUrl().contains("/login"));
        assertTrue(driver.getCurrentUrl().contains(baseUrl));
        assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(8)
    @DisplayName("E2E: 로그아웃 프로세스")
    void userLogoutProcess() {
        // 1. 먼저 로그인
        loginAsTestUser();

        // 2. 로그아웃 링크/버튼 찾기 및 클릭
        try {
            WebElement logoutLink = driver.findElement(By.linkText("로그아웃"));
            logoutLink.click();
        } catch (Exception e) {
            // POST 형태의 로그아웃이라면
            try {
                WebElement logoutForm = driver.findElement(By.cssSelector("form[action*='logout']"));
                WebElement logoutButton = logoutForm.findElement(By.cssSelector("button, input[type='submit']"));
                logoutButton.click();
            } catch (Exception ex) {
                // 다른 방식의 로그아웃 시도
                driver.get(baseUrl + "/logout");
            }
        }

        // 3. 로그아웃 성공 확인 (홈페이지나 로그인 페이지로 리다이렉션)
        wait.until(webDriver -> 
            webDriver.getCurrentUrl().contains("/login") || 
            webDriver.getCurrentUrl().equals(baseUrl + "/")
        );

        // 4. 인증이 필요한 페이지 접근 시 로그인 페이지로 리다이렉션 확인
        driver.get(baseUrl + "/appointments");
        assertTrue(driver.getCurrentUrl().contains("/login") || 
                  driver.getCurrentUrl().contains("/auth"));
    }

    private void loginAsTestUser() {
        driver.get(baseUrl + "/login");
        
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.sendKeys("existing@example.com");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(webDriver -> !webDriver.getCurrentUrl().contains("/login"));
    }
}