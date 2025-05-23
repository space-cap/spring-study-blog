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
		
		System.out.println("id : " + id);
		System.out.println("password : " + password);
		
		model.addAttribute("id", id);
		model.addAttribute("password", password);
		
		//return "forward:/userInfo.jsp";
		return "redirect:/userInfo.jsp";

	}
	
}
