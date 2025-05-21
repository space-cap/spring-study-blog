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
public class Dice2Controller {
	
	@RequestMapping(value = "/dice2", method = RequestMethod.GET)
	public String dice2(Model model) throws IOException {
		
		int idx1 = (int)(Math.random() * 6) + 1;
		int idx2 = (int)(Math.random() * 6) + 1;
		
		model.addAttribute("idx1", idx1);
		model.addAttribute("idx2", idx2);
		
		return "dice2";
	}
	
}
