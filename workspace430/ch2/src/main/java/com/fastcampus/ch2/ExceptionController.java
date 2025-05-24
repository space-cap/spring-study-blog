package com.fastcampus.ch2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExceptionController {
	@RequestMapping("/ex")
	public String main() throws Exception {
		try {
			throw new Exception("예외가 발생했습니다. zzz");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "error";
		}
	}
	
	@RequestMapping("/ex2")
	public String main2() throws Exception {
		try {
			throw new Exception("예외가 발생했습니다. zzz");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "error";
		}
	}
}
