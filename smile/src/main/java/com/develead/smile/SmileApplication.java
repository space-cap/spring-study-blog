package com.develead.smile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // [수정] 스케줄링 기능 활성화
public class SmileApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmileApplication.class, args);
	}

}
