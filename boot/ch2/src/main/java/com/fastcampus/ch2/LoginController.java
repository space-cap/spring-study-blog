package com.fastcampus.ch2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인 관련 요청을 처리하는 컨트롤러
 * 메인 페이지, 로그인 페이지, 로그인 처리, 사용자 정보 페이지를 담당
 */
@Controller
public class LoginController {

    // 하드코딩된 사용자 정보 (실제 프로젝트에서는 데이터베이스 사용)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "1234";
    private static final String USER_NAME = "관리자";
    private static final String USER_EMAIL = "admin@example.com";

    /**
     * 메인 페이지 요청 처리
     * URL: GET /
     * @return index.html 템플릿 반환
     */
    @GetMapping("/")
    public String index() {
        System.out.println("메인 페이지 요청됨");
        return "index"; // templates/index.html 반환
    }

    /**
     * 로그인 페이지 요청 처리
     * URL: GET /login
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return login.html 템플릿 반환
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        System.out.println("로그인 페이지 요청됨");
        return "login"; // templates/login.html 반환
    }

    /**
     * 로그인 처리 요청
     * URL: POST /login-process
     * @param username 사용자가 입력한 아이디 (form의 name="username"에서 전달)
     * @param password 사용자가 입력한 비밀번호 (form의 name="password"에서 전달)
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return 로그인 성공 시 user-info.html, 실패 시 login.html 반환
     */
    @PostMapping("/login-process")
    public String loginProcess(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model) {

        System.out.println("로그인 처리 요청됨");
        System.out.println("입력된 아이디: " + username);
        System.out.println("입력된 비밀번호: " + password);

        // 아이디와 비밀번호 검증
        if (isValidUser(username, password)) {
            // 로그인 성공
            System.out.println("로그인 성공!");

            // 사용자 정보를 모델에 추가하여 user-info.html에 전달
            model.addAttribute("username", username);
            model.addAttribute("name", USER_NAME);
            model.addAttribute("email", USER_EMAIL);

            return "user-info"; // templates/user-info.html 반환

        } else {
            // 로그인 실패
            System.out.println("로그인 실패!");

            // 에러 메시지를 모델에 추가하여 login.html에 전달
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");

            return "login"; // templates/login.html 반환 (에러 메시지와 함께)
        }
    }

    /**
     * 사용자 정보 페이지 직접 접근 처리
     * URL: GET /user-info
     * 로그인 없이 직접 접근하는 경우 로그인 페이지로 리다이렉트
     * @return 로그인 페이지로 리다이렉트
     */
    @GetMapping("/user-info")
    public String userInfoPage() {
        System.out.println("사용자 정보 페이지 직접 접근 시도");
        // 실제 프로젝트에서는 세션 체크 등의 인증 로직이 필요
        return "redirect:/login"; // 로그인 페이지로 리다이렉트
    }

    /**
     * 사용자 인증을 확인하는 메서드
     * @param username 입력받은 아이디
     * @param password 입력받은 비밀번호
     * @return 인증 성공 여부 (true: 성공, false: 실패)
     */
    private boolean isValidUser(String username, String password) {
        // null 체크
        if (username == null || password == null) {
            return false;
        }

        // 하드코딩된 계정 정보와 비교
        // 실제 프로젝트에서는 데이터베이스에서 사용자 정보를 조회하여 비교
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}

