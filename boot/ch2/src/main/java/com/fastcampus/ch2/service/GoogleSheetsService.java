package com.fastcampus.ch2.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Landing Form Application";
    private static final String SPREADSHEET_ID = "1LpRJoc_TDTen4Ov4221K596uEJd-FM2GUpVWclz-3FM"; // 실제 스프레드시트 ID로 변경
    private static final String RANGE = "Sheet1!A:C"; // 데이터 범위
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    /**
     * Google Sheets 서비스 객체 생성
     */
    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * 스프레드시트에 데이터 추가
     */
    public void appendData(String name, String phone, String registrationTime) {
        try {
            Sheets service = getSheetsService();

            // 추가할 데이터 준비
            List<Object> row = Arrays.asList(name, phone, registrationTime);
            List<List<Object>> values = Arrays.asList(row);

            ValueRange body = new ValueRange().setValues(values);

            // 데이터 추가 실행
            service.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            System.out.println("Google Sheets에 데이터 추가 완료: " + name + ", " + phone);

        } catch (Exception e) {
            System.err.println("Google Sheets 데이터 추가 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 스프레드시트에서 모든 데이터 읽기 (테스트용)
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

