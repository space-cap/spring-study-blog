package com.fastcampus.ch3.di1;

import java.io.FileReader;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    static Object getObject(String key) {
        Properties prop = new Properties();
        Class clazz = null;
        try {
            prop.load(new FileReader("config.txt"));
            String className = prop.getProperty(key);
            clazz = Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clazz.newInstance();
    }
}
