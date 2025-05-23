package com.fastcampus.ch2;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
	public String login(HttpServletRequest request, Model model) {
		
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		
		if (id == null || id.equals("")) {
			model.addAttribute("errorMessage", "아이디를 입력하세요.");
			return "login";
		}
		
		model.addAttribute("id", id);
		model.addAttribute("password", password);
		
		String tid = "lee";
		String tpassword = "1234";
		
		if (id.equals(tid) && password.equals(tpassword)) {
			return "userinfo";
		} else {
			model.addAttribute("errorMessage", "아이디 또는 비밀번호가 틀립니다.");
			return "redirect:/login";
		}

	}
	
}
