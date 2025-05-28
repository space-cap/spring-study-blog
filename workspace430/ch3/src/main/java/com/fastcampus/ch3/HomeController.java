
package com.fastcampus.ch3;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

//	private final DbInfoPrinter dbInfoPrinter;
//
//	// 생성자 주입 방식 (권장)
//	public HomeController(DbInfoPrinter dbInfoPrinter) {
//		this.dbInfoPrinter = dbInfoPrinter;
//	}
//
//	// application.properties에 정의된 spring.datasource.username 속성 주입
//	@Value("${spring.datasource.username}")
//	private String dbUsername;
//
//	// application.properties에 정의된 spring.datasource.password 속성 주입
//	// 보안상 비밀번호는 실제 화면에 출력하지 않도록 주의하세요!
//	@Value("${spring.datasource.password}")
//	private String dbPassword;
//
//	@RequestMapping(value = "/h2", method = RequestMethod.GET)
//	public String home2(Locale locale, Model model) {
//
//		System.out.println("DB Username: " + dbUsername);
//        System.out.println("DB Password: " + dbPassword); // 콘솔 출력도 주의
//		return "home";
//	}

}
