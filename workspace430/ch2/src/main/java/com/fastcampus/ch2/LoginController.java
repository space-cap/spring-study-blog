package com.fastcampus.ch2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(LoginInfo loginInfo, HttpServletResponse response, Model model, 
			RedirectAttributes redirectAttributes) {
		
		//String id = request.getParameter("id");
		//String password = request.getParameter("password");
		//String rememberId = request.getParameter("rememberId");
		
		String id = loginInfo.getId();
		String password = loginInfo.getPassword();
		boolean rememberId = loginInfo.isRememberId();
		
		System.out.println("id : " + id);
		System.out.println("password : " + password);
		System.out.println("rememberId : " + rememberId);
		
		
		
		if(rememberId) {
			// 쿠키를 생성
			Cookie cookie = new Cookie("id", id);
			cookie.setPath("/"); // 쿠키가 모든 경로에서 유효
			cookie.setMaxAge(30 * 60); // 30분
			response.addCookie(cookie);
			
			//checked
			Cookie cookie2 = new Cookie("isChecked", "checked");
			cookie2.setPath("/"); // 쿠키가 모든 경로에서 유효
			cookie2.setMaxAge(30 * 60); // 30분
			response.addCookie(cookie2);
		} else {
			System.out.println("rememberId : null 이다.");
			
			Cookie cookie = new Cookie("id", "");
			cookie.setPath("/"); // 쿠키가 모든 경로에서 유효
			cookie.setMaxAge(0); // 쿠키 삭제
			response.addCookie(cookie);
			
			Cookie cookie2 = new Cookie("isChecked", "");
			cookie2.setPath("/");
			cookie2.setMaxAge(0);
			response.addCookie(cookie2);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("password", password);
		
		// 로그인 처리		
		if (checkLogin(id, password)) {
			return "forward:/userInfo.jsp";
		} else {
			//redirectAttributes.addAttribute("errorMessage", "로그인 실패");
			//return "redirect:/login.jsp";
			try {
	            String encodedMessage = URLEncoder.encode("로그인에 실패했습니다", "UTF-8");
	            return "redirect:/login.jsp?errorMessage=" + encodedMessage;
	            //redirectAttributes.addFlashAttribute("errorMessage", encodedMessage);
	            //return "redirect:/login.jsp";
	            
	            //redirectAttributes.addAttribute("errorMessage", encodedMessage);
	            //redirectAttributes.addFlashAttribute("errorMessage", "로그인에 실패했습니다");
	            //return "redirect:/login.jsp";
	        } catch (UnsupportedEncodingException e) {
	            return "redirect:/login.jsp?errorMessage=Login Failed&id=" + id;
	        }
		}
		
		//return "redirect:/userInfo.jsp?key=val";
	}
	
	private boolean checkLogin(String id, String pw) {
		return id.equals("steve") && pw.equals("1234");
	}
}
