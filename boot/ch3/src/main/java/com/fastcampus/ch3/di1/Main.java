package com.fastcampus.ch3.di1;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

class Car {}
class SportsCar extends Car {}
class Truck extends Car {}
class Engine {}
class Door {}

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        Car car = (Car)getObject("car");
        System.out.println("car: " + car);
    }

    static Object getObject(String key) throws Exception {
        Properties prop = new Properties();
        Class clazz = null;
        prop.load(new FileReader("config.txt"));
        String className = prop.getProperty(key);
        clazz = Class.forName(className);

        return clazz.newInstance();
    }
}
