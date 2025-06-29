package com.fastcampus.ch2.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Landing Form Application";

    @Value("${google.sheets.spreadsheet.id:default-id}")
    private String SPREADSHEET_ID;

    private static final String RANGE = "시트1!A1:C";

    // 테스트를 위해 Sheets 객체를 주입받을 수 있도록 수정
    private Sheets sheetsService;

    /**
     * 생성자 - 테스트에서 Mock 객체 주입 가능
     */
    public GoogleSheetsService() {
        // 기본 생성자
    }

    /**
     * 테스트용 생성자 - Mock Sheets 객체 주입
     */
    public GoogleSheetsService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService != null) {
            return sheetsService; // 테스트에서 주입된 Mock 객체 사용
        }

        // resources/credentials.json 파일 읽기
        InputStream credentialsStream = getClass().getClassLoader()
                .getResourceAsStream("credentials.json");

        if (credentialsStream == null) {
            throw new IOException("credentials.json 파일을 찾을 수 없습니다.");
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void appendData(String name, String phone, String registrationTime) {
        try {
            Sheets service = getSheetsService();

            List<Object> row = Arrays.asList(name, phone, registrationTime);
            List<List<Object>> values = Arrays.asList(row);

            ValueRange body = new ValueRange().setValues(values);

            service.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            System.out.println("Google Sheets 데이터 추가 성공: " + name);

        } catch (Exception e) {
            System.err.println("Google Sheets 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 스프레드시트에서 데이터 읽기 (테스트용)
     */
    public List<List<Object>> readData() {
        try {
            Sheets service = getSheetsService();
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE)
                    .execute();

            return response.getValues();

        } catch (Exception e) {
            System.err.println("Google Sheets 데이터 읽기 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
