package com.fastcampus.ch3.diCopy1;

import java.io.FileReader;
import java.util.Properties;

class Car {
}

class SportsCar extends Car {
}

class Truck extends Car {
}

class Engine {
}

public class MiniSpringContext {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello, Mini Spring!");

		Car car = getCar();
	}

	public static Car getCar() throws Exception {
		// config.txt를 읽어서 Properties에 저장
	    Properties p = new Properties();
	    p.load(new FileReader("config.txt"));

	    // 클래스 객체(설계도)를 얻어서
	    Class clazz = Class.forName(p.getProperty("car"));
	    return (Car) clazz.newInstance(); // 객체를 생성해서 반환
	}
	
}
