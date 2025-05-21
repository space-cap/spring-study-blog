package com.fastcampus.ch2;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class YoilController {
	@RequestMapping(value = "/yoil", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		Calendar cal = (Calendar) model.getAttribute("inputTime");

		Date date = cal.getTime();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);

		int idx = cal.get(Calendar.DAY_OF_WEEK);
		char yoil = "일월화수목금토일".charAt(idx - 1);

		model.addAttribute("inputDate", formattedDate);
		model.addAttribute("yoil", yoil);

		return "yoil";
	}

	@ModelAttribute("inputTime")
	public void getTime(MyYoil myDate, Model model) {
		Calendar cal = Calendar.getInstance();
		cal.set(myDate.getYear(), myDate.getMonth() - 1, myDate.getDay());
		model.addAttribute("inputTime", cal);
	}

}
