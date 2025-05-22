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
		Properties p = new Properties();
		p.load(new FileReader("config.txt"));

		return null;
	}
}
