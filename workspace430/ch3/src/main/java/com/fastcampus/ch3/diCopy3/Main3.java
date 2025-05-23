package com.fastcampus.ch3.diCopy3;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.reflect.ClassPath;

@Component("car3")
class Car {
}

@Component("sportsCar3")
class SportsCar extends Car {
}

@Component("truck3")
class Truck extends Car {
}


class Engine {
}

class AppContext {
	private Map map;
	
	AppContext() {
		map = new HashMap();
        doComponentScan();
	}
	
	private void doComponentScan() {
		try {
            // 1. 패키지내의 클래스 목록을 가져온다.
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);

            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy3");

            // 2. 반복문으로 클래스를 하나씩 읽어와서 @Component 애너테이션이 붙어있는지 확인
            for (ClassPath.ClassInfo classInfo : set) {
                Class clazz = classInfo.load();
                Component component = (Component) clazz.getAnnotation(Component.class);
                // 3. @Componet 애너테이션이 붙어있으면 객체를 생성해서 Map에 저장
                if (component != null) {
                    String id = StringUtils.uncapitalize(classInfo.getSimpleName());
                    map.put(id, clazz.getDeclaredConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public Object getBean(String key) {
		return map.get(key);
	}
	
	public Object getBean(Class clazz) {
		for(Object obj : map.values()) {
			if(clazz.isInstance(obj)) {
				return obj;
			}
		}
		return null;
	}
}


public class Main3 {
	public static void main(String[] args) {
		System.out.println("Hello, Main3!");
		
		AppContext ac = new AppContext();
		
		Car car = (Car) ac.getBean("car"); // by Name으로 객체를 검색
        Car car2 = (Car) ac.getBean(Car.class); // by Type으로 객체를 검색

        Engine engine = (Engine) ac.getBean("engine");
        Engine engine2 = (Engine) ac.getBean(Engine.class);

        System.out.println("car = " + car);
        System.out.println("engine = " + engine);

        System.out.println("car2 = " + car2);
        System.out.println("engine2 = " + engine2);
	}
}
