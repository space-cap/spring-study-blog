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

//		Car car1 = getCar();
//		Car car2 = getCar();
//		Car car3 = getCar();
//		System.out.println("car1 = " + car1);
//		System.out.println("car2 = " + car2);
//		System.out.println("car3 = " + car3);
		
		Car car = (Car) getObject("car");
	    Engine engine = (Engine) getObject("engine");
	    System.out.println("car = " + car);
	    System.out.println("engine = " + engine);
		
	}

	static Object getObject(String key) throws Exception {
	    // config.txt를 읽어서 Properties에 저장
	    Properties p = new Properties();
	    p.load(new FileReader("src/main/resources/config.txt"));

	    // 클래스 객체(설계도)를 얻어서
	    Class<?> clazz = Class.forName(p.getProperty(key));
	    return clazz.getDeclaredConstructor().newInstance();
	}
	
	public static Car getCar() throws Exception {

		// config.txt를 읽어서 Properties에 저장
		Properties p = new Properties();
		p.load(new FileReader("src/main/resources/config.txt"));
				
	    // 클래스 객체(설계도)를 얻어서
	    Class<?> clazz = Class.forName(p.getProperty("car"));
	    return (Car) clazz.getDeclaredConstructor().newInstance();  // 객체를 생성해서 반환
		//return (Car) Class.forName(p.getProperty("car")).newInstance();  // 객체를 생성해서 반환
	}
	
}
