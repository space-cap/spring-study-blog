package com.fastcampus.ch3.di3;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

class Car {}
class Engine {}
class TubroEngine extends Engine {}
class Door {}

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // AC를 생성 - AC의 설정파일은 AppConfig.class 자바설정
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        Car car = (Car)ac.getBean("car"); // byName 객체(빈)을 조회
        System.out.println("car = " + car);

        /*Car car2 = (Car)ac.getBean("car");
        Car car3 = (Car)ac.getBean("car");
        System.out.println("car2 = " + car2);
        System.out.println("car3 = " + car3);*/

        Map<String, String> env = System.getenv();
        System.out.println("env = " + env);

    }
}
