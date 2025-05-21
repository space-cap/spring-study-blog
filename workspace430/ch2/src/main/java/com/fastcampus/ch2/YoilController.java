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
	public String home(MyYoil date, Model model) {
		return "yoil";
	}

	// 메서드에 @ModelAttribute를 붙이면
	// 1. 자동으로 메서드를 호출
	// 2. 호출 결과를 Model에 자동 저장
	@ModelAttribute("yoil")
	private char getYoil(MyYoil myDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(myDate.getYear(), myDate.getMonth() - 1, myDate.getDay());
		int idx = cal.get(Calendar.DAY_OF_WEEK);

		return "일월화수목금토일".charAt(idx);
	}

	@ModelAttribute("inputDate")
	private String getDate(Locale locale, MyYoil myDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(myDate.getYear(), myDate.getMonth() - 1, myDate.getDay());
		// 시간 정보를 0으로 초기화
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date date = cal.getTime();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);

		return formattedDate;
	}

}
