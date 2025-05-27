package com.fastcampus.ch2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

	@GetMapping("/login")
	public String loginForm(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		if( session.getAttribute("id") != null) {
			return "redirect:/"; // 이미 로그인한 상태이면, 메인 화면으로 이동
		}
		
		return "loginForm";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.invalidate(); // 세션 무효화
		
		return "redirect:/";
	}

	@PostMapping("/login")
	public String login(String id, String pwd, boolean rememberId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		System.out.println("id : " + id);
		System.out.println("password : " + pwd);
		System.out.println("rememberId : " + rememberId);

		// 로그인 처리
		if (!checkLogin(id, pwd)) {
			String msg = URLEncoder.encode("id 또는 pwd가 일치하지 않습니다.", "utf-8");
			return "redirect:/login/login?msg=" + msg;
		}

		// 세션에 id 저장
		// request.getSession().setAttribute("id", id);
		HttpSession session = request.getSession();
		session.setAttribute("id", id);

		// 아이디 기억하기 체크박스가 선택되었으면 쿠키에 저장
		if (rememberId) {
			setCookie(response, "id", id, 30 * 60); // 30분 동안 유효
			setCookie(response, "isChecked", "checked", 30 * 60); // 30분 동안 유효
		} else {
			setCookie(response, "id", id, 0); // 쿠키 삭제
			setCookie(response, "isChecked", "checked", 0); // 쿠키 삭제
		}

		String toURL = request.getParameter("toURL");
		if (toURL != null && !toURL.isEmpty()) {
			// 로그인 성공 후, 원래 요청한 URL로 리다이렉트
			return "redirect:" + toURL;
		}

		return "redirect:/";

	}

	@RequestMapping(value = "/loginx", method = RequestMethod.GET)
	public String loginx() {
		return "login";
	}

	@RequestMapping(value = "/loginx", method = RequestMethod.POST)
	public String loginx(LoginInfo loginInfo, HttpServletResponse response, Model model,
			RedirectAttributes redirectAttributes) {

		// String id = request.getParameter("id");
		// String password = request.getParameter("password");
		// String rememberId = request.getParameter("rememberId");

		String id = loginInfo.getId();
		String password = loginInfo.getPassword();
		boolean rememberId = loginInfo.isRememberId();

		System.out.println("id : " + id);
		System.out.println("password : " + password);
		System.out.println("rememberId : " + rememberId);

		model.addAttribute("id", id);
		model.addAttribute("password", password);

		// 로그인 처리
		if (checkLogin(id, password)) {
			if (rememberId) {
				setCookie(response, "id", id, 30 * 60); // 30분 동안 유효
				setCookie(response, "isChecked", "checked", 30 * 60); // 30분 동안 유효
			} else {
				setCookie(response, "id", id, 0);
				setCookie(response, "isChecked", "checked", 0);
			}

			return "forward:/userInfo.jsp";
		} else {
			// redirectAttributes.addAttribute("errorMessage", "로그인 실패");
			// return "redirect:/login.jsp";
			try {
				String encodedMessage = URLEncoder.encode("로그인에 실패했습니다", "UTF-8");
				return "redirect:/login.jsp?errorMessage=" + encodedMessage;
				// redirectAttributes.addFlashAttribute("errorMessage", encodedMessage);
				// return "redirect:/login.jsp";

				// redirectAttributes.addAttribute("errorMessage", encodedMessage);
				// redirectAttributes.addFlashAttribute("errorMessage", "로그인에 실패했습니다");
				// return "redirect:/login.jsp";
			} catch (UnsupportedEncodingException e) {
				return "redirect:/login.jsp?errorMessage=Login Failed&id=" + id;
			}
		}

		// return "redirect:/userInfo.jsp?key=val";
	}

	private boolean checkLogin(String id, String pw) {
		return id.equals("steve") && pw.equals("1234");
	}

	private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/"); // 쿠키가 모든 경로에서 유효
		cookie.setMaxAge(maxAge); // 쿠키의 유효 기간 설정
		response.addCookie(cookie);
	}
}
