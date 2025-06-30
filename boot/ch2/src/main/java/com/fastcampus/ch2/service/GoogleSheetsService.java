package com.fastcampus.ch2.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Landing Form Application";

    @Value("${google.sheets.spreadsheet.id:default-id}")
    private String SPREADSHEET_ID;

    // 헤더 정보 상수
    private static final List<Object> HEADER_ROW = Arrays.asList("이름", "전화번호", "등록시간", "상담완료");

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

    /**
     * Google Sheets 서비스 객체 생성
     */
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

    /**
     * 지정된 시트가 존재하는지 확인
     * @param sheetName 확인할 시트 이름
     * @return 시트 존재 여부
     */
    private boolean checkSheetExists(String sheetName) {
        try {
            Sheets service = getSheetsService();
            Spreadsheet spreadsheet = service.spreadsheets()
                    .get(SPREADSHEET_ID)
                    .execute();

            // 모든 시트를 순회하며 이름 확인
            for (Sheet sheet : spreadsheet.getSheets()) {
                if (sheet.getProperties().getTitle().equals(sheetName)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            System.err.println("시트 존재 확인 중 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * 새로운 시트 생성
     * @param sheetName 생성할 시트 이름
     * @return 생성된 시트의 ID, 실패 시 -1
     */
    private int createSheet(String sheetName) {
        try {
            Sheets service = getSheetsService();

            // 새 시트 생성 요청 생성
            AddSheetRequest addSheetRequest = new AddSheetRequest()
                    .setProperties(new SheetProperties().setTitle(sheetName));

            Request request = new Request().setAddSheet(addSheetRequest);

            // BatchUpdate 요청으로 시트 생성
            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(request));

            BatchUpdateSpreadsheetResponse response = service.spreadsheets()
                    .batchUpdate(SPREADSHEET_ID, batchRequest)
                    .execute();

            // 생성된 시트의 ID 반환
            int sheetId = response.getReplies().get(0).getAddSheet().getProperties().getSheetId();
            System.out.println("새 시트 생성 성공: " + sheetName + " (ID: " + sheetId + ")");
            return sheetId;

        } catch (Exception e) {
            System.err.println("시트 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 시트에 헤더 행 설정
     * @param sheetName 헤더를 설정할 시트 이름
     * @return 성공 여부
     */
    private boolean setHeaderRow(String sheetName) {
        try {
            Sheets service = getSheetsService();

            // 헤더 데이터 설정
            List<List<Object>> headerValues = Collections.singletonList(HEADER_ROW);
            ValueRange headerBody = new ValueRange().setValues(headerValues);

            // 헤더 행 범위 (A1:C1)
            String headerRange = "'" + sheetName + "'!A1:D1";

            // 헤더 데이터 업데이트
            service.spreadsheets().values()
                    .update(SPREADSHEET_ID, headerRange, headerBody)
                    .setValueInputOption("RAW")
                    .execute();

            // 상담완료 컬럼(D열)에 체크박스 추가
            //setCheckboxValidation(sheetName);

            System.out.println("헤더 행 설정 완료: " + sheetName);
            return true;

        } catch (Exception e) {
            System.err.println("헤더 행 설정 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 상담완료 컬럼에 체크박스 데이터 검증 설정
     * @param sheetName 시트 이름
     */
    private void setCheckboxValidation(String sheetName) {
        try {
            Sheets service = getSheetsService();

            // 체크박스 데이터 검증 규칙 생성
            DataValidationRule checkboxRule = new DataValidationRule()
                    .setCondition(new BooleanCondition()
                            .setType("BOOLEAN"))
                    .setInputMessage("체크박스를 선택하세요")
                    .setShowCustomUi(true);

            // 시트 ID 가져오기
            int sheetId = getSheetId(sheetName);
            if (sheetId == -1) return;

            // D열 전체에 체크박스 적용 (D2부터 D1000까지)
            GridRange range = new GridRange()
                    .setSheetId(sheetId)
                    .setStartColumnIndex(3) // D열 (0부터 시작)
                    .setEndColumnIndex(4)   // D열까지
                    .setStartRowIndex(1)    // 2행부터 (헤더 제외)
                    .setEndRowIndex(1000);  // 1000행까지

            // 데이터 검증 설정 요청
            SetDataValidationRequest validationRequest = new SetDataValidationRequest()
                    .setRange(range)
                    .setRule(checkboxRule);

            Request request = new Request().setSetDataValidation(validationRequest);

            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(request));

            service.spreadsheets().batchUpdate(SPREADSHEET_ID, batchRequest).execute();

            System.out.println("체크박스 설정 완료: " + sheetName);

        } catch (Exception e) {
            System.err.println("체크박스 설정 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 시트 ID 조회
     * @param sheetName 시트 이름
     * @return 시트 ID, 찾지 못하면 -1
     */
    private int getSheetId(String sheetName) {
        try {
            Sheets service = getSheetsService();
            Spreadsheet spreadsheet = service.spreadsheets()
                    .get(SPREADSHEET_ID)
                    .execute();

            for (Sheet sheet : spreadsheet.getSheets()) {
                if (sheet.getProperties().getTitle().equals(sheetName)) {
                    return sheet.getProperties().getSheetId();
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("시트 ID 조회 중 오류: " + e.getMessage());
            return -1;
        }
    }


    /**
     * 특정 행의 상담완료 컬럼(D열)에 체크박스 설정
     * @param sheetName 시트 이름
     * @param rowIndex 행 번호 (0부터 시작, 헤더는 0번째 행)
     */
    private void setCheckboxForRow(String sheetName, int rowIndex) {
        try {
            Sheets service = getSheetsService();

            // 시트 ID 가져오기
            int sheetId = getSheetId(sheetName);
            if (sheetId == -1) {
                System.err.println("시트 ID를 찾을 수 없습니다: " + sheetName);
                return;
            }

            // 체크박스 데이터 검증 규칙 생성
            DataValidationRule checkboxRule = new DataValidationRule()
                    .setCondition(new BooleanCondition()
                            .setType("BOOLEAN"))
                    .setInputMessage("상담 완료 여부를 체크하세요")
                    .setShowCustomUi(true);

            // 특정 행의 D열에만 체크박스 적용
            GridRange range = new GridRange()
                    .setSheetId(sheetId)
                    .setStartColumnIndex(3)     // D열 (0부터 시작)
                    .setEndColumnIndex(4)       // D열까지
                    .setStartRowIndex(rowIndex) // 지정된 행
                    .setEndRowIndex(rowIndex + 1); // 해당 행만

            // 데이터 검증 설정 요청
            SetDataValidationRequest validationRequest = new SetDataValidationRequest()
                    .setRange(range)
                    .setRule(checkboxRule);

            Request request = new Request().setSetDataValidation(validationRequest);

            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(request));

            service.spreadsheets().batchUpdate(SPREADSHEET_ID, batchRequest).execute();

            System.out.println("체크박스 설정 완료 - 시트: " + sheetName + ", 행: " + (rowIndex + 1));

        } catch (Exception e) {
            System.err.println("체크박스 설정 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 시트의 현재 데이터 행 수 조회 (헤더 제외)
     * @param sheetName 시트 이름
     * @return 데이터 행 수
     */
    private int getDataRowCount(String sheetName) {
        try {
            Sheets service = getSheetsService();
            String range = "'" + sheetName + "'!A:A"; // A열 전체 조회

            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return 0; // 헤더도 없는 경우
            }

            // 헤더를 제외한 데이터 행 수 반환
            return Math.max(0, values.size() - 1);

        } catch (Exception e) {
            System.err.println("데이터 행 수 조회 중 오류: " + e.getMessage());
            return 0;
        }
    }


    /**
     * 시트 확인 및 생성 (헤더 포함)
     * @param sheetName 확인/생성할 시트 이름
     * @return 성공 여부
     */
    private boolean ensureSheetExists(String sheetName) {
        // 시트가 이미 존재하는지 확인
        if (checkSheetExists(sheetName)) {
            System.out.println("시트가 이미 존재합니다: " + sheetName);
            return true;
        }

        // 시트가 없으면 생성
        int sheetId = createSheet(sheetName);
        if (sheetId == -1) {
            return false;
        }

        // 헤더 행 설정
        return setHeaderRow(sheetName);
    }

    /**
     * 지정된 시트에 데이터 추가
     * @param sheetName 데이터를 추가할 시트 이름
     * @param name 이름
     * @param phone 전화번호
     * @param registrationTime 등록시간
     * @return 성공 여부
     */
    @Async("googleSheetsTaskExecutor")
    public CompletableFuture<Boolean> appendDataToSheet(String sheetName,
                                                        String name, String phone,
                                                        String registrationTime,
                                                        boolean consultationComplete) {
        try {
            // 시트 존재 확인 및 생성
            if (!ensureSheetExists(sheetName)) {
                System.err.println("시트 생성 또는 확인 실패: " + sheetName);
                return CompletableFuture.completedFuture(false);
            }

            // 현재 데이터 행 수 확인 (새로 추가될 행의 인덱스 계산용)
            int currentRowCount = getDataRowCount(sheetName);
            int newRowIndex = currentRowCount + 1; // 헤더(0) + 기존 데이터 행들 + 새 행

            Sheets service = getSheetsService();

            // 추가할 데이터 행 생성
            List<Object> row = Arrays.asList(name, phone, registrationTime, consultationComplete);
            List<List<Object>> values = Collections.singletonList(row);

            ValueRange body = new ValueRange().setValues(values);

            // 데이터 범위 설정 (A2부터 시작하여 헤더 아래에 추가)
            String range = "'" + sheetName + "'!A2:D";

            // 데이터 추가
            service.spreadsheets().values()
                    .append(SPREADSHEET_ID, range, body)
                    //.setValueInputOption("RAW")
                    .setValueInputOption("USER_ENTERED") // 체크박스를 위해 USER_ENTERED 사용
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            // 새로 추가된 행에 체크박스 설정
            setCheckboxForRow(sheetName, newRowIndex);
            System.out.println("데이터 추가 및 체크박스 설정 완료 - 시트: " + sheetName + ", 이름: " + name + ", 행: " + (newRowIndex + 1));

            System.out.println("데이터 추가 성공 - 시트: " + sheetName + ", 이름: " + name);
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            System.err.println("데이터 추가 중 오류: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 기존 appendData 메서드 - 기본 시트("시트1") 사용
     * @param name 이름
     * @param phone 전화번호
     * @param registrationTime 등록시간
     * @return 성공 여부
     */
    public CompletableFuture<Boolean> appendData(String name, String phone, String registrationTime) {
        return appendDataToSheet("시트1", name, phone, registrationTime, false);
    }

    /**
     * 지정된 시트에서 데이터 읽기
     * @param sheetName 읽을 시트 이름
     * @return 시트의 모든 데이터
     */
    public List<List<Object>> readDataFromSheet(String sheetName) {
        try {
            // 시트 존재 확인
            if (!checkSheetExists(sheetName)) {
                System.err.println("시트가 존재하지 않습니다: " + sheetName);
                return Collections.emptyList();
            }

            Sheets service = getSheetsService();
            String range = "'" + sheetName + "'!A1:C";

            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            return values != null ? values : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("데이터 읽기 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 기존 readData 메서드 - 기본 시트("시트1") 사용
     * @return 기본 시트의 모든 데이터
     */
    public List<List<Object>> readData() {
        return readDataFromSheet("시트1");
    }

    /**
     * 스프레드시트의 모든 시트 이름 조회
     * @return 시트 이름 목록
     */
    public List<String> getAllSheetNames() {
        try {
            Sheets service = getSheetsService();
            Spreadsheet spreadsheet = service.spreadsheets()
                    .get(SPREADSHEET_ID)
                    .execute();

            return spreadsheet.getSheets().stream()
                    .map(sheet -> sheet.getProperties().getTitle())
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            System.err.println("시트 목록 조회 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
