package com.fastcampus.ch3.di1;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

class Car {}
class SportCar extends Car {}
class Truck extends Car {}
class Engin {}
class Door {}

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        Car car = (Car)getObject("car");
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
