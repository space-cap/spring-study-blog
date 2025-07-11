package com.fastcampus.ch2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Ch2Application {
	public static void main(String[] args) {
		SpringApplication.run(Ch2Application.class, args);
	}

}
