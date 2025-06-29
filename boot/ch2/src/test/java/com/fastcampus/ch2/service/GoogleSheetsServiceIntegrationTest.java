package com.fastcampus.ch2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GoogleSheetsService 통합 테스트
 * 실제 Google Sheets API와 연동하여 테스트
 * 주의: credentials.json 파일과 실제 스프레드시트가 필요합니다
 */
@SpringBootTest
@ActiveProfiles("test") // 테스트 프로필 사용
@DisplayName("GoogleSheetsService 통합 테스트")
public class GoogleSheetsServiceIntegrationTest {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    /**
     * 실제 Google Sheets와 연동 테스트
     * 주의: 실제 API 호출이므로 credentials.json이 필요합니다
     */
    @Test
    @DisplayName("실제 Google Sheets 연동 테스트")
    void realGoogleSheetsIntegration() {
        // Given (준비)
        String testName = "테스트사용자";
        String testPhone = "010-9999-8888";
        String testTime = "2025. 6. 29. 오전 9:30:00";

        // When (실행)
        boolean appendResult = googleSheetsService.appendData(testName, testPhone, testTime);

        // Then (검증)
        assertTrue(appendResult, "데이터 추가가 성공해야 합니다");

        // 데이터 읽기 테스트
        List<List<Object>> data = googleSheetsService.readData();
        assertNotNull(data, "데이터를 읽을 수 있어야 합니다");
        assertFalse(data.isEmpty(), "데이터가 존재해야 합니다");
    }
}
