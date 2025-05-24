package com.fastcampus.ch2;

import java.io.FileNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExceptionController {
	
//	@ExceptionHandler(Exception.class)
//	public String catcher(Exception e, Model model) {
//		System.out.println("ExceptionController.catcher() called");
//		e.printStackTrace();
//		model.addAttribute("ex", e);
//		return "error";
//	}
	
	@RequestMapping("/ex")
	public String main() throws Exception {
		throw new Exception("예외가 발생했습니다. zzz");
	}
	
	@RequestMapping("/ex2")
	public String main2() throws Exception {
		//throw new Exception("예외가 발생했습니다. zzz");
		throw new FileNotFoundException("파일을 찾을 수 없습니다. zzz");
	}
}
