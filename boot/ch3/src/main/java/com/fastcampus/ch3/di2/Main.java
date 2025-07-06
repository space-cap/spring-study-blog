package com.fastcampus.ch3.di2;

class Car {}
class SportsCar extends Car {}
class Truck extends Car {}
class Engine {}
class Door {}

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        //AppContext ac = new AppContext();
        AppContext ac = new AppContext(AppConfig.class);


    }
}
