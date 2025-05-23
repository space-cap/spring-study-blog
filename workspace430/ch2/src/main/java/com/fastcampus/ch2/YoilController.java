package com.fastcampus.ch2;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
		System.out.println("home() called");
		
		Calendar cal = (Calendar) model.getAttribute("inputTime");

		String appVer = (String) model.getAttribute("appVer");
		System.out.println("appVer = " + appVer);

		Object categoriesObj = model.getAttribute("categories");
		if (categoriesObj instanceof List) {
			List<String> list = (List<String>) categoriesObj;
		    for (String s : list) {
		        System.out.println("s = " + s);
		    }
		}


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
		System.out.println("getTime() called");
		
		Calendar cal = Calendar.getInstance();
		cal.set(myDate.getYear(), myDate.getMonth() - 1, myDate.getDay());
		model.addAttribute("inputTime", cal);
	}

	@Controller
	public class SomeController {

		@ModelAttribute
		public void addCommonAttributes(Model model) {
			model.addAttribute("siteName", "My Site");
		}
	}

	@ModelAttribute("categories")
	public List<String> categories() {
		return Arrays.asList("Book", "Music", "Movie");
	}

}
