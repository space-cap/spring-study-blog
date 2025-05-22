package com.fastcampus.ch3;

import java.io.FileReader;
import java.util.Properties;

public class MiniSpringContext {
	public static void main(String[] args) {
		System.out.println("Hello, Mini Spring!");
		
		Car car = getCar();
	}
	
	public static Car getCar() {
		Properties p = new Properties();
	    p.load(new FileReader("config.txt"));
		
		return null;
	}
}
