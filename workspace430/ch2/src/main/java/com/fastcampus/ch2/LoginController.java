package com.fastcampus.ch2;

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

@Controller
public class LoginController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String rememberId = request.getParameter("rememberId");
		
		System.out.println("id : " + id);
		System.out.println("password : " + password);
		System.out.println("rememberId : " + rememberId);
		
		model.addAttribute("id", id);
		model.addAttribute("password", password);
		
		
		
		Cookie cookie = new Cookie("name", "king");
		cookie.setPath("/"); // 쿠키가 모든 경로에서 유효
		cookie.setMaxAge(30 * 60); // 30분
		//response.addCookie(cookie);

		
		
		if (id.equals("steve") && password.equals("1234")) {
			return "forward:/userInfo.jsp";
		} else {
			model.addAttribute("errorMessage", "로그인 실패");
			return "forward:/login.jsp";
		}
		
		//return "redirect:/userInfo.jsp?key=val";
	}
	
}
