package com.fastcampus.ch2;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Dice2Controller {
	public static final int DICE_SIDES = 6;

	@RequestMapping(value = "/dice2", method = RequestMethod.GET)
	public String dice2(Model model) throws IOException {

		int dice1 = (int) (Math.random() * DICE_SIDES) + 1;
		int dice2 = (int) (Math.random() * DICE_SIDES) + 1;

		model.addAttribute("dice1", dice1);
		model.addAttribute("dice2", dice2);

		return "dice2";
	}

}
