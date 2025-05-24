package com.fastcampus.ch2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExceptionController {
	@RequestMapping("/ex")
	public void main() throws Exception {
		throw new Exception("예외가 발생했습니다. zzz");
	}
}
