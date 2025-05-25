package com.fastcampus.ch2;

import java.io.FileNotFoundException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice // 모든 @Controller에 적용되는 ExceptionHandler
public class GlobalCatcher {
	@ExceptionHandler({NullPointerException.class})
	public String catcher2(Exception ex, Model m) {
		System.out.println("GlobalCatcher.catcher2() called");
		m.addAttribute("ex", ex);
		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String catcher(Exception ex, Model m) {
		System.out.println("GlobalCatcher.catcher() called");
		m.addAttribute("ex", ex);
		return "error";
	}
	
	@ExceptionHandler(FileNotFoundException.class)
    public ModelAndView handleFileNotFoundException(FileNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error2");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
