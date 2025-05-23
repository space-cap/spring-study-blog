package com.fastcampus.ch2;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CookieController {
	private static final Logger logger = LoggerFactory.getLogger(CookieController.class);
	@RequestMapping(value = "/coo", method = RequestMethod.GET)
	public String home(Locale locale, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("hello! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		model.addAttribute("name", "영현");
		
		Cookie cookie = new Cookie("name", "king");
		cookie.setPath("/"); // 쿠키가 모든 경로에서 유효
		cookie.setMaxAge(30 * 60); // 30분
		response.addCookie(cookie);

		HttpSession session = request.getSession();
	    session.setAttribute("userId", "steve");
		
		return "hello";
	}
	
}
