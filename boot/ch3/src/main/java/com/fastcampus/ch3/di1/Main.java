package com.fastcampus.ch3.di1;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    static Object getObject(String key) {
        Properties prop = new Properties();
        prop.load("config.txt");
        String className = prop.getProperty(key);
        Class clazz = Class.forName(className);

        return clazz.newInstance();
    }
}
