package com.ezlevup.smilechat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring Boot 애플리케이션 기본 테스트
 */
@SpringBootTest
@TestPropertySource(properties = "spring.threads.virtual.enabled=false")
class DentalChatApplicationTests {

    @Test
    void contextLoads() {
        // Spring Context가 정상적으로 로드되는지 확인
    }
}
