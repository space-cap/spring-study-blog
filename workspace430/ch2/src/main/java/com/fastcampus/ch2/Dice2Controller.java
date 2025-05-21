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
	public static final int DICE_SIDES = 6;
	public static final int NUM_OF_DICE = 2;

	@RequestMapping(value = "/dice2", method = RequestMethod.GET)
	public String dice2(Model model) throws IOException {

		int dice1 = (int) (Math.random() * DICE_SIDES) + 1;
		int dice2 = (int) (Math.random() * DICE_SIDES) + 1;

		model.addAttribute("idx1", dice1);
		model.addAttribute("idx2", dice2);

		return "dice2";
	}

}
