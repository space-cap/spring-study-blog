package com.fastcampus.ch2.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
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
    private static final String SPREADSHEET_ID = "1LpRJoc_TDTen4Ov4221K596uEJd-FM2GUpVWclz-3FM"; // 실제 ID로 변경
    private static final String RANGE = "Sheet1!A:C";

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
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
}
