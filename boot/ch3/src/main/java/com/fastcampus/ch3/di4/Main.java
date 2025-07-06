package com.fastcampus.ch3.di4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Main.class, args);
        String[] beanNames = ctx.getBeanDefinitionNames();

        Arrays.sort(beanNames);
        Arrays.stream(beanNames).forEach(System.out::println);

    }
}
