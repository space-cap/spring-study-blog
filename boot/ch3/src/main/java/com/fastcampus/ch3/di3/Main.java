package com.fastcampus.ch3.di3;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class Car {}
class Engine {}
class TubroEngine extends Engine {}
class Door {}

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // AC를 생성 - AC의 설정파일은 AppConfig.class 자바설정
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);


    }
}
