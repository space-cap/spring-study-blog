package com.fastcampus.ch2.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleSheetsService 테스트")
class GoogleSheetsServiceTest {

    // Mock 객체들
    @Mock
    private Sheets mockSheets;

    @Mock
    private Sheets.Spreadsheets mockSpreadsheets;

    @Mock
    private Sheets.Spreadsheets.Values mockValues;

    @Mock
    private Sheets.Spreadsheets.Values.Append mockAppend;

    @Mock
    private Sheets.Spreadsheets.Values.Get mockGet;

    // 테스트 대상 서비스
    private GoogleSheetsService googleSheetsService;

    // 테스트 데이터
    private final String TEST_NAME = "홍길동";
    private final String TEST_PHONE = "010-1234-5678";
    private final String TEST_TIME = "2025. 6. 29. 오전 9:00:00";

    /**
     * 각 테스트 실행 전 초기화
     */
    @BeforeEach
    void setUp() {
        // Mock 객체를 주입하여 GoogleSheetsService 생성
        googleSheetsService = new GoogleSheetsService(mockSheets);

        // Mock 객체들의 체인 설정
        when(mockSheets.spreadsheets()).thenReturn(mockSpreadsheets);
        when(mockSpreadsheets.values()).thenReturn(mockValues);
    }


    /**
     * 데이터 추가 성공 테스트
     */
    //@Test
    //@DisplayName("데이터 추가 성공 테스트")
    void appendData_Success() throws Exception {
        // Given (준비)
        AppendValuesResponse mockResponse = new AppendValuesResponse();

        // Mock 객체 동작 설정
        when(mockValues.append(anyString(), anyString(), any(ValueRange.class)))
                .thenReturn(mockAppend);
        when(mockAppend.setValueInputOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.setInsertDataOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.execute())
                .thenReturn(mockResponse);

        // When (실행)
        CompletableFuture<Boolean> result = googleSheetsService.appendData(TEST_NAME, TEST_PHONE, TEST_TIME);

        // Then (검증)
        assertTrue(result.get(5, TimeUnit.SECONDS), "데이터 추가가 성공해야 합니다");

        // Mock 객체 호출 검증
        verify(mockValues).append(anyString(), eq("시트1!A1:C"), any(ValueRange.class));
        verify(mockAppend).setValueInputOption("RAW");
        verify(mockAppend).setInsertDataOption("INSERT_ROWS");
        verify(mockAppend).execute();
    }


    /**
     * 데이터 추가 실패 테스트
     */
    //@Test
    //@DisplayName("데이터 추가 실패 테스트")
    void appendData_Failure() throws Exception {
        // Given (준비)
        when(mockValues.append(anyString(), anyString(), any(ValueRange.class)))
                .thenReturn(mockAppend);
        when(mockAppend.setValueInputOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.setInsertDataOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.execute())
                .thenThrow(new IOException("네트워크 오류"));

        // When (실행)
        CompletableFuture<Boolean> result = googleSheetsService.appendData(TEST_NAME, TEST_PHONE, TEST_TIME);

        // Then (검증)
        assertFalse(result.get(5, TimeUnit.SECONDS), "데이터 추가가 실패해야 합니다");
    }


    /**
     * 데이터 읽기 성공 테스트
     */
    //@Test
    //@DisplayName("데이터 읽기 성공 테스트")
    void readData_Success() throws IOException {
        // Given (준비)
        List<List<Object>> expectedData = Arrays.asList(
                Arrays.asList("이름", "전화번호", "등록시간"),
                Arrays.asList(TEST_NAME, TEST_PHONE, TEST_TIME)
        );

        ValueRange mockValueRange = new ValueRange();
        mockValueRange.setValues(expectedData);

        // Mock 객체 동작 설정
        when(mockValues.get(anyString(), anyString()))
                .thenReturn(mockGet);
        when(mockGet.execute())
                .thenReturn(mockValueRange);

        // When (실행)
        List<List<Object>> result = googleSheetsService.readData();

        // Then (검증)
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertEquals(2, result.size(), "2개의 행이 있어야 합니다");
        assertEquals(expectedData, result, "예상 데이터와 일치해야 합니다");

        // Mock 객체 호출 검증
        verify(mockValues).get(anyString(), eq("Sheet1!A:C"));
        verify(mockGet).execute();
    }

    /**
     * 데이터 읽기 실패 테스트
     */
    //@Test
    //@DisplayName("데이터 읽기 실패 테스트")
    void readData_Failure() throws IOException {
        // Given (준비)
        when(mockValues.get(anyString(), anyString()))
                .thenReturn(mockGet);
        when(mockGet.execute())
                .thenThrow(new IOException("권한 오류"));

        // When (실행)
        List<List<Object>> result = googleSheetsService.readData();

        // Then (검증)
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertTrue(result.isEmpty(), "빈 리스트를 반환해야 합니다");
    }


    /**
     * null 값 처리 테스트
     */
    //@Test
    //@DisplayName("null 값 처리 테스트")
    void appendData_WithNullValues() throws Exception {
        // Given (준비)
        AppendValuesResponse mockResponse = new AppendValuesResponse();

        when(mockValues.append(anyString(), anyString(), any(ValueRange.class)))
                .thenReturn(mockAppend);
        when(mockAppend.setValueInputOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.setInsertDataOption(anyString()))
                .thenReturn(mockAppend);
        when(mockAppend.execute())
                .thenReturn(mockResponse);

        // When (실행)
        CompletableFuture<Boolean> result = googleSheetsService.appendData(null, null, null);

        // Then (검증)
        assertTrue(result.get(5,TimeUnit.SECONDS), "null 값도 처리되어야 합니다");
    }

    //@Test
    void appendData() {
    }

    //@Test
    void readData() {
    }
}