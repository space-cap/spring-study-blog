package com.fastcampus.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

class Car {}

@Configuration
public class MyConfiguration {
    @Bean
    @Scope("prototype")
    //@Scope("singleton")
    Car car() {
        return new Car();
    }
}
