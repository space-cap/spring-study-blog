package com.fastcampus.ch2;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class KingController {
	int getRandomInt(int n) {
		return (int)(Math.random() * n) + 1;
	}
	
	@RequestMapping(value = "/king", method = RequestMethod.GET)
	public void king(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int idx1 = getRandomInt(6);
		int idx2 = getRandomInt(6);
		
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"utf-8\">");
		out.println("<title>King</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>King</h1>");
		out.println("<h2>주사위 1: " + idx1 + "</h2>");
		out.println("<h2>주사위 2: " + idx2 + "</h2>");
		out.println("</body>");
		out.println("</html>");
		
	}
	
}
