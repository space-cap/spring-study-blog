package com.fastcampus.ch3.diCopy2;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class Car {
}

class SportsCar extends Car {
}

class Truck extends Car {
}

class Engine {
}

class AppContext {
	Map map; // 객체 저장소

    AppContext() {

        try {
            Properties p = new Properties();
            p.load(new FileReader("src/main/resources/config.txt"));

            // Properties에 저장된 내용을 Map에 저장
            map = new HashMap(p); // 지정된 Map의 모든 요소를 포함하는 HashMap을 생성.

            // 반복문으로 클래스 이름을 얻어서 객체를 생성해서 다시 map에 저장
            for (Object key : map.keySet()) {
                // Map에 저장된 key를 이용해서 class 정보를 가져와야 한다.
            	Class<?> clazz = Class.forName((String) map.get(key));

                // 객체를 만들어서 Map에 저장
                map.put(key, clazz.getDeclaredConstructor().newInstance());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Object getBean(String key) {
        return map.get(key);
    }
}

public class Main2 {
	public static void main(String[] args) {
		System.out.println("Hello, Main2!");

		AppContext ac = new AppContext();

		Car car = (Car) ac.getBean("car");
		Engine engine = (Engine) ac.getBean("engine");
		System.out.println("car = " + car);
		System.out.println("engine = " + engine);

	}

}
