package com.fastcampus.ch2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DiceController {
	public static final int DICE_SIDES = 6;
	public static final int MAX_NUM_OF_DICE = 4;
	public static final int MIN_NUM_OF_DICE = 2;

	@RequestMapping(value = "/dice", method = RequestMethod.GET)
	public void dice(HttpServletRequest request, HttpServletResponse response) throws IOException {

		int diceCount = MIN_NUM_OF_DICE;
		if (request.getParameter("dc") != null) {
			diceCount = Integer.parseInt(request.getParameter("dc"));
			if (diceCount < MIN_NUM_OF_DICE || diceCount > MAX_NUM_OF_DICE) {
				diceCount = MIN_NUM_OF_DICE;
			}
		}
		System.out.println("diceCount=" + diceCount);

		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		ArrayList<Integer> diceResults = new ArrayList<>();
		for (int i = 0; i < diceCount; i++) {
			int dice = (int) (Math.random() * DICE_SIDES) + 1;
			diceResults.add(dice);
		}

		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\">");
		out.println("<title>주사위</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>주사위</h1>");
		for (var result : diceResults) {
			int dice = result;
			out.println("<img src='resources/dice/dice" + dice + ".jpg'>");
		}
		out.println("</body>");
		out.println("</html>");
	}

}
