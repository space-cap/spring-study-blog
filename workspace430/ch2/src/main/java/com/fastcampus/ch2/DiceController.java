package com.fastcampus.ch2;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class DiceController {
	
	@RequestMapping(value = "/dice", method = RequestMethod.GET)
	public void dice(HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    
		int idx1 = (int)(Math.random() * 6) + 1;
		int idx2 = (int)(Math.random() * 6) + 1;
		
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\">");
		out.println("<title>주사위</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>주사위</h1>");
		out.println("<img src='resources/dice/dice"+ idx1 +".jpg'>");
		out.println("<img src='resources/dice/dice"+ idx2 +".jpg'>");
		out.println("</body>");
		out.println("</html>");
	}
	
}
